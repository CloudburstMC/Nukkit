package cn.nukkit.network.session;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.network.CompressionProvider;
import cn.nukkit.network.NetherNetInterface;
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

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

@Log4j2
public class NetherNetPlayerSession extends SimpleChannelInboundHandler<Object> implements NetworkPlayerSession {

    private final NetherNetInterface server;
    @Getter
    private final Channel channel;

    private final Queue<DataPacket> inbound = PlatformDependent.newSpscQueue();
    private final Queue<DataPacket> outbound = PlatformDependent.newMpscQueue();

    private Player player;
    @Getter
    private volatile String disconnectReason = null;

    private CompressionProvider compressionIn;
    private CompressionProvider compressionOut;
    private boolean compressionInitialized;

    public NetherNetPlayerSession(NetherNetInterface server, Channel channel) {
        this.server = server;
        this.channel = channel;
        this.compressionIn = this.compressionOut = CompressionProvider.NONE;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        this.disconnect("Channel Inactive");
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof TransportDisconnectReason) {
            this.disconnect(evt.toString());
        }
    }

    @Override
    public void disconnect(String reason) {
        if (this.disconnectReason != null) {
            return;
        }

        this.disconnectReason = reason;

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
        DataPacket packet;
        while ((packet = this.inbound.poll()) != null) {
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
    public void setEncryption(SecretKey encryptionKey, Cipher encryptionCipher, Cipher decryptionCipher) {
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
        if (!(msg instanceof ByteBuf)) {
            return;
        }

        ByteBuf buffer = (ByteBuf) msg;
        int len = buffer.readableBytes();
        if (len > 12582912) {
            Server.getInstance().getLogger().error("Received too big packet: " + len);
            if (this.player != null) {
                this.player.close("Too big packet");
            }
            return;
        }

        boolean prefixed = this.compressionInitialized;

        CompressionProvider compressionIn = this.compressionIn;

        if (prefixed) {
            compressionIn = CompressionProvider.byPrefix(buffer.readByte());
        }

        byte[] packetBuffer = new byte[buffer.readableBytes()];

        buffer.readBytes(packetBuffer);

        try {
            this.server.getNetwork().processBatch(packetBuffer, this.inbound, compressionIn);
        } catch (Exception e) {
            this.disconnect("Sent malformed packet");
            log.error("Unable to process batch packet from {}", (this.player == null ? this.channel.remoteAddress() : this.player.getName()), e);
        }
    }
}
