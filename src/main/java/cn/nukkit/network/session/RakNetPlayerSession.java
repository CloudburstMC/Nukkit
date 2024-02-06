package cn.nukkit.network.session;

import cn.nukkit.Nukkit;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.network.CompressionProvider;
import cn.nukkit.network.RakNetInterface;
import cn.nukkit.network.protocol.BatchPacket;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.DisconnectPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.utils.BinaryStream;
import com.google.common.base.Preconditions;
import com.nukkitx.natives.sha256.Sha256;
import com.nukkitx.natives.util.Natives;
import com.nukkitx.network.raknet.*;
import com.nukkitx.network.util.DisconnectReason;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.util.internal.PlatformDependent;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.message.FormattedMessage;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Log4j2
public class RakNetPlayerSession implements NetworkPlayerSession, RakNetSessionListener {

    private final RakNetInterface server;
    private final RakNetServerSession session;

    private final Queue<DataPacket> inbound = PlatformDependent.newSpscQueue();
    private final Queue<DataPacket> outbound = PlatformDependent.newMpscQueue();
    private final ScheduledFuture<?> tickFuture;

    private Player player;
    private String disconnectReason = null;

    private CompressionProvider compressionOut = CompressionProvider.NONE;
    private boolean compressionInitialized;

    private SecretKey encryptionKey;
    private Cipher encryptionCipher;
    private Cipher decryptionCipher;
    private final AtomicLong sentEncryptedPacketCount = new AtomicLong();

    public RakNetPlayerSession(RakNetInterface server, RakNetServerSession session) {
        this.server = server;
        this.session = session;
        this.tickFuture = session.getEventLoop().scheduleAtFixedRate(this::networkTick, 0, 50, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onEncapsulated(EncapsulatedPacket packet) {
        ByteBuf buffer = packet.getBuffer();
        short packetId = buffer.readUnsignedByte();
        if (packetId == 0xfe) {
            byte[] packetBuffer;

            CompressionProvider compressionIn = CompressionProvider.NONE;

            if (this.decryptionCipher != null) {
                try {
                    ByteBuffer buf = buffer.nioBuffer();
                    this.decryptionCipher.update(buf, buf.duplicate());
                } catch (Exception ex) {
                    Server.getInstance().getLogger().error("Packet decryption failed for " + player.getName(), ex);
                    return;
                }

                if (this.compressionInitialized) {
                    compressionIn = CompressionProvider.byPrefix(buffer.readByte());
                }

                packetBuffer = new byte[buffer.readableBytes() - 8];
            } else {
                if (this.compressionInitialized) {
                    compressionIn = CompressionProvider.byPrefix(buffer.readByte());
                }

                packetBuffer = new byte[buffer.readableBytes()];
            }

            buffer.readBytes(packetBuffer);

            try {
                this.server.getNetwork().processBatch(packetBuffer, this.inbound, compressionIn);
            } catch (Exception e) {
                this.disconnect("Sent malformed packet");
                log.error("[{}] Unable to process batch packet", (this.player == null ? this.session.getAddress() : this.player.getName()), e);
            }
        } else if (Nukkit.DEBUG > 1) {
            log.debug("Unknown EncapsulatedPacket: " +packetId);
        }
    }

    @Override
    public void onDirect(ByteBuf byteBuf) {
        // We don't allow any direct packets so ignore.
    }

    @Override
    public void onSessionChangeState(RakNetState rakNetState) {
    }

    @Override
    public void onDisconnect(DisconnectReason reason) {
        if (reason == DisconnectReason.TIMED_OUT) {
            this.disconnect("Timed out");
        } else {
            this.disconnect("Disconnected from Server");
        }
    }

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
        this.session.getEventLoop().schedule(() -> this.session.close(), 10, TimeUnit.MILLISECONDS);
    }

    @Override
    public void sendPacket(DataPacket packet) {
        if (this.session.isClosed()) {
            return;
        }

        if (packet.pid() != ProtocolInfo.BATCH_PACKET) {
            packet.tryEncode();
        }
        this.outbound.offer(packet);
    }

    @Override
    public void sendImmediatePacket(DataPacket packet, Runnable callback) {
        if (this.session.isClosed()) {
            return;
        }

        this.sendPacket(packet);
        this.session.getEventLoop().execute(() -> {
            this.networkTick();
            callback.run();
        });
    }

    @Override
    public void flush() {
        this.session.getEventLoop().execute(this::networkTick);
    }

    private void networkTick() {
        if (this.session.isClosed()) {
            return;
        }

        try {
            List<DataPacket> toBatch = new ObjectArrayList<>();
            DataPacket packet;
            while ((packet = this.outbound.poll()) != null) {
                if (packet instanceof DisconnectPacket) {
                    BinaryStream batched = new BinaryStream();
                    byte[] buf = packet.getBuffer();
                    batched.putUnsignedVarInt(buf.length);
                    batched.put(buf);

                    try {
                        this.sendPacket(this.compressionOut.compress(batched, Server.getInstance().networkCompressionLevel), RakNetPriority.IMMEDIATE);
                    } catch (Exception e) {
                        log.error("Unable to compress disconnect packet", e);
                    }
                    return; // Disconnected
                } else if (packet instanceof BatchPacket) {
                    if (!toBatch.isEmpty()) {
                        this.sendPackets(toBatch);
                        toBatch.clear();
                    }

                    this.sendPacket(((BatchPacket) packet).payload, RakNetPriority.MEDIUM);
                } else {
                    toBatch.add(packet);
                }
            }

            if (!toBatch.isEmpty()) {
                this.sendPackets(toBatch);
            }
        } catch (Throwable e) {
            log.error("[{}] Failed to tick RakNetPlayerSession", this.session.getAddress(), e);
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
            this.sendPacket(this.compressionOut.compress(batched, Server.getInstance().networkCompressionLevel), RakNetPriority.MEDIUM);
        } catch (Exception e) {
            log.error("Unable to compress batched packets", e);
        }
    }

    private void sendPacket(byte[] compressedPayload, RakNetPriority priority) {
        ByteBuf finalPayload = ByteBufAllocator.DEFAULT.directBuffer((this.compressionInitialized ? 10 : 9) + compressedPayload.length); // prefix(1)+id(1)+encryption(8)+data
        finalPayload.writeByte(0xfe);

        if (this.encryptionCipher != null) {
            try {
                byte[] fullPayload = this.compressionInitialized ? new byte[compressedPayload.length + 1] : compressedPayload;
                if (this.compressionInitialized) {
                    fullPayload[0] = this.compressionOut.getPrefix();
                    System.arraycopy(compressedPayload, 0, fullPayload, 1, compressedPayload.length);
                }
                ByteBuf compressed = Unpooled.wrappedBuffer(fullPayload);
                ByteBuffer trailer = ByteBuffer.wrap(this.generateTrailer(compressed));
                ByteBuffer outBuffer = finalPayload.internalNioBuffer(1, compressed.readableBytes() + 8);
                ByteBuffer inBuffer = compressed.internalNioBuffer(compressed.readerIndex(), compressed.readableBytes());
                this.encryptionCipher.update(inBuffer, outBuffer);
                this.encryptionCipher.update(trailer, outBuffer);
                finalPayload.writerIndex(finalPayload.writerIndex() + compressed.readableBytes() + 8);
            } catch (Exception ex) {
                Server.getInstance().getLogger().error("Packet encryption failed for " + player.getName(), ex);
            }
        } else {
            if (this.compressionInitialized) {
                finalPayload.writeByte(this.compressionOut.getPrefix());
            }

            finalPayload.writeBytes(compressedPayload);
        }

        this.session.send(finalPayload, priority);
    }

    @Override
    public void setCompression(CompressionProvider compression) {
        Preconditions.checkNotNull(compression, "compression");
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

    public RakNetServerSession getRakNetSession() {
        return this.session;
    }

    public String getDisconnectReason() {
        return this.disconnectReason;
    }

    @Override
    public void setEncryption(SecretKey encryptionKey, Cipher encryptionCipher, Cipher decryptionCipher) {
        this.encryptionKey = encryptionKey;
        this.encryptionCipher = encryptionCipher;
        this.decryptionCipher = decryptionCipher;
    }

    private static final ThreadLocal<Sha256> HASH_LOCAL = ThreadLocal.withInitial(Natives.SHA_256);

    private byte[] generateTrailer(ByteBuf buf) {
        Sha256 hash = HASH_LOCAL.get();
        ByteBuf counterBuf = ByteBufAllocator.DEFAULT.directBuffer(8);
        try {
            counterBuf.writeLongLE(this.sentEncryptedPacketCount.getAndIncrement());
            ByteBuffer keyBuffer = ByteBuffer.wrap(this.encryptionKey.getEncoded());
            hash.update(counterBuf.internalNioBuffer(0, 8));
            hash.update(buf.internalNioBuffer(buf.readerIndex(), buf.readableBytes()));
            hash.update(keyBuffer);
            byte[] digested = hash.digest();
            return Arrays.copyOf(digested, 8);
        } finally {
            counterBuf.release();
            hash.reset();
        }
    }
}
