package cn.nukkit.network.session;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.network.CompressionProvider;
import cn.nukkit.network.RakNetInterface;
import cn.nukkit.network.protocol.BatchPacket;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.utils.BinaryStream;
import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.internal.PlatformDependent;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.message.FormattedMessage;
import org.cloudburstmc.netty.channel.raknet.packet.RakMessage;

import java.net.ProtocolException;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Log4j2
public class RakNetPlayerSession extends SimpleChannelInboundHandler<RakMessage> implements NetworkPlayerSession {

    private final RakNetInterface server;
    private final Channel channel;

    private final Queue<DataPacket> inbound = PlatformDependent.newSpscQueue();
    private final Queue<DataPacket> outbound = PlatformDependent.newMpscQueue();
    private final ScheduledFuture<?> tickFuture;

    private Player player;
    private String disconnectReason = null;

    private CompressionProvider compression = CompressionProvider.NONE;

    public RakNetPlayerSession(RakNetInterface server, Channel channel) {
        this.server = server;
        this.channel = channel;
        this.tickFuture = channel.eventLoop().scheduleAtFixedRate(this::networkTick, 0, 50, TimeUnit.MILLISECONDS);
    }

//    @Override
//    public void onSessionChangeState(RakNetState rakNetState) {
//    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        this.disconnect("Disconnected");
    }
//
//    @Override
//    public void onDisconnect(DisconnectReason reason) {
//        if (reason == DisconnectReason.TIMED_OUT) {
//            this.disconnect("Timed out");
//        } else {
//            this.disconnect("Disconnected from Server");
//        }
//    }

    @Override
    public void disconnect(String reason) {
        if (this.disconnectReason != null) {
            return;
        }

        this.disconnectReason = reason;
        if (this.tickFuture != null) {
            this.tickFuture.cancel(false);
        }

        // Give it a short time to make sure cancel message is delivered
        this.channel.eventLoop().schedule(() -> this.channel.close(), 10, TimeUnit.MILLISECONDS);
    }

    @Override
    public void sendPacket(DataPacket packet) {
        if (this.channel.isActive()) {
            packet.tryEncode();
            this.outbound.offer(packet);
        }
    }

    @Override
    public void sendImmediatePacket(DataPacket packet, Runnable callback) {
        if (!this.channel.isActive()) {
            return;
        }


        this.sendPacket(packet);
        this.channel.eventLoop().execute(() -> {
            this.networkTick();
            callback.run();
        });
    }

    private void networkTick() {
        if (!this.channel.isActive()) {
            return;
        }

        List<DataPacket> toBatch = new ObjectArrayList<>();
        DataPacket packet;
        while ((packet = this.outbound.poll()) != null) {
            if (packet.pid() == ProtocolInfo.BATCH_PACKET) {
                if (!toBatch.isEmpty()) {
                    this.sendPackets(toBatch);
                    toBatch.clear();
                }

                this.sendPacket(((BatchPacket) packet).payload);
            } else {
                toBatch.add(packet);
            }
        }

        if (!toBatch.isEmpty()) {
            this.sendPackets(toBatch);
        }
    }

    public void serverTick() {
        DataPacket packet;
        while ((packet = this.inbound.poll()) != null) {
            try {
                this.player.handleDataPacket(packet);
            } catch (Exception e) {
                log.error(new FormattedMessage("An error occurred whilst handling {} for {}",
                        new Object[]{packet.getClass().getSimpleName(), this.player.getName()}, e));
            }
        }
    }

    private void sendPackets(Collection<DataPacket> packets) {
        BinaryStream batched = new BinaryStream();
        for (DataPacket packet : packets) {
            Preconditions.checkArgument(!(packet instanceof BatchPacket), "Cannot batch BatchPacket");
            Preconditions.checkState(packet.isEncoded, "Packet should have already been encoded");
            byte[] buf = packet.getBuffer();
            batched.putUnsignedVarInt(buf.length);
            batched.put(buf);
        }

        try {
            this.sendPacket(this.compression.compress(batched, Server.getInstance().networkCompressionLevel));
        } catch (Exception e) {
            log.error("Unable to compress batched packets", e);
        }
    }

    private void sendPacket(byte[] payload) {
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.ioBuffer(1 + payload.length);
        byteBuf.writeByte(0xfe);
        byteBuf.writeBytes(payload);
        this.channel.writeAndFlush(byteBuf);
    }

    @Override
    public void setCompression(CompressionProvider compression) {
        Preconditions.checkNotNull(compression);
        this.compression = compression;
    }

    @Override
    public CompressionProvider getCompression() {
        return this.compression;
    }

    public void setPlayer(Player player) {
        Preconditions.checkArgument(this.player == null && player != null);
        this.player = player;
    }

    @Override
    public Player getPlayer() {
        return this.player;
    }

    public Channel getChannel() {
        return this.channel;
    }

    public String getDisconnectReason() {
        return this.disconnectReason;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RakMessage msg) throws Exception {
        ByteBuf buffer = msg.content();
        short packetId = buffer.readUnsignedByte();
        if (packetId == 0xfe) {
            byte[] packetBuffer = new byte[buffer.readableBytes()];
            buffer.readBytes(packetBuffer);

            try {
                this.server.getNetwork().processBatch(packetBuffer, this.inbound, this.getCompression());
            } catch (ProtocolException e) {
                this.disconnect("Sent malformed packet");
                log.error("Unable to process batch packet", e);
            }
        }
    }
}
