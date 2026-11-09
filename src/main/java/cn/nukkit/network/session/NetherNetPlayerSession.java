package cn.nukkit.network.session;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.network.CompressionProvider;
import cn.nukkit.network.NetherNetInterface;
import cn.nukkit.network.Network;
import cn.nukkit.network.protocol.BatchPacket;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.DisconnectPacket;
import cn.nukkit.utils.BinaryStream;
import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.internal.PlatformDependent;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.message.FormattedMessage;
import org.cloudburstmc.netty.channel.TransportDisconnectReason;
import org.cloudburstmc.netty.channel.nethernet.NetherNetChannel;

import java.net.ProtocolException;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Log4j2
public class NetherNetPlayerSession extends SimpleChannelInboundHandler<Object> implements NetworkPlayerSession {

    private static final int MAX_INBOUND_PACKETS = 2048;
    private static final int MAX_INBOUND_BYTES = 8 * 1024 * 1024;
    private static final int MAX_PACKETS_PER_TICK = 128;

    private final Supplier<Network> network;
    @Getter
    private final Channel channel;

    private final Queue<DataPacket> inbound = new ArrayDeque<>();
    private int inboundBytes;
    private final Queue<DataPacket> outbound = PlatformDependent.newMpscQueue();

    private Player player;
    @Getter
    private volatile String disconnectReason = null;

    private CompressionProvider compressionIn;
    private CompressionProvider compressionOut;
    private boolean compressionInitialized;

    public NetherNetPlayerSession(NetherNetInterface source, Channel channel) {
        this(source::getNetwork, channel);
    }

    private NetherNetPlayerSession(Supplier<Network> network, Channel channel) {
        this.network = network;
        this.channel = channel;
        this.compressionIn = this.compressionOut = CompressionProvider.NONE;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        this.stopReceiving("Channel Inactive");
        this.outbound.clear();
        ctx.fireChannelInactive();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof TransportDisconnectReason) {
            this.disconnect(evt.toString());
        }
    }

    @Override
    public void disconnect(String reason) {
        if (!this.stopReceiving(reason)) {
            return;
        }

        // Give it a short time to make sure cancel message is delivered
        this.channel.eventLoop().schedule(() -> this.channel.close(), 5, TimeUnit.MILLISECONDS);
    }

    @Override
    public void sendPacket(DataPacket packet) {
        if (!this.channel.isActive()) {
            return;
        }

        this.outbound.offer(packet);
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

    @Override
    public void flush() {
        if (!this.channel.isActive()) {
            return;
        }

        this.channel.eventLoop().execute(this::networkTick);
    }

    private void networkTick() {
        if (!this.channel.isActive()) {
            return;
        }

        try {
            List<DataPacket> toBatch = new ObjectArrayList<>();
            DataPacket packet;
            while ((packet = this.outbound.poll()) != null) {
                if (!(packet instanceof BatchPacket)) {
                    packet.tryEncode();
                }

                if (packet instanceof DisconnectPacket) {
                    byte[] buf = packet.getBuffer();
                    BinaryStream batched = new BinaryStream(new byte[5 + buf.length]).reset();
                    batched.putUnsignedVarInt(buf.length);
                    batched.put(buf);

                    try {
                        this.sendPacket(this.compressionOut.compress(batched, Server.getInstance().networkCompressionLevel));
                    } catch (Exception e) {
                        log.error("Unable to send disconnect packet", e);
                    }
                    return; // Disconnected
                } else if (packet instanceof BatchPacket) {
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
        } catch (Throwable e) {
            log.error("Failed to tick RakNetPlayerSession for {}", this.channel.remoteAddress(), e);
        }
    }

    public void serverTick() {
        for (int i = 0; i < MAX_PACKETS_PER_TICK; i++) {
            DataPacket packet = this.pollInbound();
            if (packet == null) {
                return;
            }
            try {
                this.player.handleDataPacket(packet);
            } catch (Throwable e) {
                log.error(new FormattedMessage("An error occurred whilst handling {} for {}",
                        new Object[]{packet.getClass().getSimpleName(), this.player.getName()}, e));
            }
        }
    }

    private void sendPackets(Collection<DataPacket> packets) {
        BinaryStream batched = new BinaryStream();
        for (DataPacket packet : packets) {
            if (packet instanceof BatchPacket) {
                throw new IllegalArgumentException("Cannot batch BatchPacket");
            }
            if (!packet.isEncoded) {
                throw new IllegalStateException("Packet should have already been encoded");
            }
            byte[] buf = packet.getBuffer();
            batched.putUnsignedVarInt(buf.length);
            batched.put(buf);
        }

        try {
            this.sendPacket(this.compressionOut.compress(batched, Server.getInstance().networkCompressionLevel));
        } catch (Exception e) {
            log.error("Unable to send packets", e);
        }
    }

    private void sendPacket(byte[] compressedPayload) {
        boolean prefixed = this.compressionInitialized;

        ByteBuf finalPayload = ByteBufAllocator.DEFAULT.directBuffer((prefixed ? 1 : 0) + compressedPayload.length); // prefix(1)+data (no packet id, no encryption)

        if (prefixed) {
            finalPayload.writeByte(this.compressionOut.getPrefix());
        }

        finalPayload.writeBytes(compressedPayload);
        this.channel.writeAndFlush(finalPayload);
    }

    @Override
    public void setCompression(CompressionProvider compression) {
        Preconditions.checkNotNull(compression, "compression");
        this.compressionIn = compression;
        this.compressionOut = compression;
        this.compressionInitialized = true;
    }

    @Override
    public CompressionProvider getCompression() {
        return this.compressionOut;
    }

    public void setPlayer(Player player) {
        Preconditions.checkArgument(this.player == null && player != null);
        this.player = player;
    }

    @Override
    public Player getPlayer() {
        return this.player;
    }

    @Override
    public long getPing() {
        if (this.channel instanceof NetherNetChannel ch) {
            try {
                return ch.getPing();
            } catch (Exception ignore) {
            }
        }
        return -1;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object msg) {
        if (this.disconnectReason != null || !(msg instanceof ByteBuf buffer)) {
            return;
        }

        try {
            if (!buffer.isReadable() || buffer.readableBytes() > MAX_INBOUND_BYTES) {
                throw new ProtocolException("Invalid batch size");
            }

            CompressionProvider compression = this.compressionInitialized ? CompressionProvider.byPrefix(buffer.readByte()) : this.compressionIn;
            if (!buffer.isReadable()) {
                throw new ProtocolException("Missing batch payload");
            }

            byte[] payload = new byte[buffer.readableBytes()];
            buffer.readBytes(payload);
            List<DataPacket> packets = new ObjectArrayList<>();
            this.network.get().processBatch(payload, packets, compression);
            if (!this.offerInbound(packets)) {
                this.disconnect("Too much queued input");
            }
        } catch (Exception e) {
            this.disconnect("Sent malformed packet");
            log.debug("Unable to process batch packet from {}", this.channel.remoteAddress(), e);
        }
    }

    private synchronized boolean offerInbound(List<DataPacket> packets) {
        if (this.disconnectReason != null || packets.size() > MAX_INBOUND_PACKETS - this.inbound.size()) {
            return false;
        }

        int remaining = MAX_INBOUND_BYTES - this.inboundBytes;
        for (DataPacket packet : packets) {
            remaining -= packet.getRawBuffer().length;
            if (remaining < 0) {
                return false;
            }
        }

        this.inbound.addAll(packets);
        this.inboundBytes = MAX_INBOUND_BYTES - remaining;
        return true;
    }

    private synchronized DataPacket pollInbound() {
        DataPacket packet = this.inbound.poll();
        if (packet != null) {
            this.inboundBytes -= packet.getRawBuffer().length;
        }
        return packet;
    }

    private synchronized boolean stopReceiving(String reason) {
        if (this.disconnectReason != null) {
            return false;
        }

        this.disconnectReason = reason;
        this.inbound.clear();
        this.inboundBytes = 0;
        return true;
    }
}
