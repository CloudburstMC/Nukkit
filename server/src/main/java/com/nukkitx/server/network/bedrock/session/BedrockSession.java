package com.nukkitx.server.network.bedrock.session;

import com.google.common.base.Preconditions;
import com.nukkitx.network.NetworkPacket;
import com.nukkitx.network.NetworkSession;
import com.nukkitx.network.raknet.RakNetPacket;
import com.nukkitx.network.raknet.session.RakNetSession;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.event.player.PlayerInitializationEvent;
import com.nukkitx.server.level.NukkitLevel;
import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.BedrockPacketCodec;
import com.nukkitx.server.network.bedrock.NetworkPacketHandler;
import com.nukkitx.server.network.bedrock.annotations.NoEncryption;
import com.nukkitx.server.network.bedrock.packet.DisconnectPacket;
import com.nukkitx.server.network.bedrock.packet.WrappedPacket;
import com.nukkitx.server.network.bedrock.session.data.AuthData;
import com.nukkitx.server.network.bedrock.session.data.ClientData;
import com.nukkitx.server.network.bedrock.wrapper.DefaultWrapperHandler;
import com.nukkitx.server.network.bedrock.wrapper.WrapperHandler;
import com.nukkitx.server.util.NativeCodeFactory;
import com.voxelwind.server.jni.hash.VoxelwindHash;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import lombok.extern.log4j.Log4j2;
import net.md_5.bungee.jni.cipher.BungeeCipher;

import javax.annotation.Nullable;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

@Log4j2
public class BedrockSession implements NetworkSession<RakNetSession> {
    private static final ThreadLocal<VoxelwindHash> hashLocal = ThreadLocal.withInitial(NativeCodeFactory.hash::newInstance);
    private static final InetSocketAddress LOOPBACK_BEDROCK = new InetSocketAddress(InetAddress.getLoopbackAddress(), 19132);
    private final Queue<BedrockPacket> currentlyQueued = new ConcurrentLinkedQueue<>();
    private final AtomicLong sentEncryptedPacketCount = new AtomicLong();
    private final NukkitServer server;
    private BedrockPacketCodec packetCodec = BedrockPacketCodec.DEFAULT;
    private NetworkPacketHandler handler = new LoginPacketHandler(this);
    private WrapperHandler wrapperHandler = DefaultWrapperHandler.DEFAULT;
    private RakNetSession connection;
    private AuthData authData;
    private ClientData clientData;
    private BungeeCipher encryptionCipher;
    private BungeeCipher decryptionCipher;
    private PlayerSession playerSession;
    private byte[] serverKey;
    private int protocolVersion;

    public BedrockSession(NukkitServer server, RakNetSession connection) {
        this.server = server;
        this.connection = connection;
    }

    public AuthData getAuthData() {
        return authData;
    }

    public void setAuthData(AuthData authData) {
        Preconditions.checkNotNull(authData, "authenticationProfile");
        this.authData = authData;
    }

    public NetworkPacketHandler getHandler() {
        return handler;
    }

    public void setHandler(NetworkPacketHandler handler) {
        checkForClosed();
        Preconditions.checkNotNull(handler, "handler");
        this.handler = handler;
    }

    void setPacketCodec(BedrockPacketCodec packetCodec) {
        this.packetCodec = packetCodec;
    }

    public BedrockPacketCodec getPacketCodec() {
        return packetCodec;
    }

    public WrapperHandler getWrapperHandler() {
        return wrapperHandler;
    }

    public void setWrapperHandler(WrapperHandler wrapperHandler) {
        checkForClosed();
        Preconditions.checkNotNull(wrapperHandler, "wrapperCompressionHandler");
        this.wrapperHandler = wrapperHandler;
    }

    private void checkForClosed() {
        Preconditions.checkState(!connection.isClosed(), "Connection has been closed!");
    }

    public void addToSendQueue(BedrockPacket packet) {
        checkForClosed();
        Preconditions.checkNotNull(packet, "packet");
        if (log.isTraceEnabled()) {
            String to = connection.getRemoteAddress().orElse(LOOPBACK_BEDROCK).toString();
            log.trace("Outbound {}: {}", to, packet);
        }

        // Verify that the packet ID exists.
        packetCodec.getId(packet);

        currentlyQueued.add(packet);
    }

    public void sendImmediatePackage(NetworkPacket packet) {
        checkForClosed();
        Preconditions.checkNotNull(packet, "packet");
        internalSendPackage(packet);
    }

    private void internalSendPackage(NetworkPacket packet) {
        if (packet instanceof BedrockPacket) {
            if (log.isTraceEnabled()) {
                String to = connection.getRemoteAddress().orElse(LOOPBACK_BEDROCK).toString();
                log.trace("Outbound {}: {}", to, packet);
            }
            WrappedPacket wrappedPacket = new WrappedPacket();
            wrappedPacket.getPackets().add((BedrockPacket) packet);
            if (packet.getClass().isAnnotationPresent(NoEncryption.class)) {
                wrappedPacket.setEncrypted(true);
            }
            packet = wrappedPacket;
        }

        if (packet instanceof WrappedPacket) {
            WrappedPacket wrappedPacket = (WrappedPacket) packet;
            ByteBuf compressed;
            if (wrappedPacket.getBatched() == null) {
                compressed = wrapperHandler.compressPackets(packetCodec, wrappedPacket.getPackets());
            } else {
                compressed = wrappedPacket.getBatched();
            }

            ByteBuf finalPayload = PooledByteBufAllocator.DEFAULT.directBuffer();
            try {
                if (encryptionCipher == null || wrappedPacket.isEncrypted()) {
                    compressed.readerIndex(0);
                    finalPayload.writeBytes(compressed);
                } else {
                    compressed.readerIndex(0);
                    byte[] trailer = generateTrailer(compressed);
                    compressed.writeBytes(trailer);

                    compressed.readerIndex(0);
                    encryptionCipher.cipher(compressed, finalPayload);
                }
            } catch (GeneralSecurityException e) {
                finalPayload.release();
                throw new RuntimeException("Unable to encipher package", e);
            } finally {
                compressed.release();
            }
            wrappedPacket.setPayload(finalPayload);
        }

        if (packet instanceof RakNetPacket) {
            connection.sendPacket((RakNetPacket) packet);
        } else {
            throw new UnsupportedOperationException("Unknown packet type sent");
        }
    }

    public void onTick() {
        if (connection.isClosed()) {
            return;
        }

        sendQueued();
    }

    private void sendQueued() {
        BedrockPacket packet;
        WrappedPacket wrapper = new WrappedPacket();
        while ((packet = currentlyQueued.poll()) != null) {
            if (packet.getClass().isAnnotationPresent(NoEncryption.class)) {
                // We hit a wrappable packet. Send the current wrapper and then send the un-wrappable packet.
                if (!wrapper.getPackets().isEmpty()) {
                    internalSendPackage(wrapper);
                    wrapper = new WrappedPacket();
                }

                internalSendPackage(packet);

                try {
                    // Delay things a tiny bit
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    log.error("Interrupted", e);
                }

                //continue;
            }/* else if (wrapper.getPackets().size() >= 100) {
                // Reached a per-batch limit on packages, send these packages now
                internalSendPackage(wrapper);
                wrapper = new WrappedPacket();

                try {
                    // Delay things a tiny bit
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    log.error("Interrupted", e);
                }
            }*/

            wrapper.getPackets().add(packet);
        }

        if (!wrapper.getPackets().isEmpty()) {
            internalSendPackage(wrapper);
        }
    }

    void enableEncryption(byte[] secretKey) {
        checkForClosed();

        serverKey = secretKey;
        byte[] iv = Arrays.copyOf(secretKey, 16);
        SecretKey key = new SecretKeySpec(secretKey, "AES");
        try {
            encryptionCipher = NativeCodeFactory.cipher.newInstance();
            decryptionCipher = NativeCodeFactory.cipher.newInstance();

            encryptionCipher.init(true, key, iv);
            decryptionCipher.init(false, key, iv);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Unable to initialize ciphers", e);
        }

        connection.setUseOrdering(true);
    }

    private byte[] generateTrailer(ByteBuf buf) {
        VoxelwindHash hash = hashLocal.get();
        ByteBuf counterBuf = PooledByteBufAllocator.DEFAULT.directBuffer(8);
        ByteBuf keyBuf = PooledByteBufAllocator.DEFAULT.directBuffer(serverKey.length);
        try {
            counterBuf.writeLongLE(sentEncryptedPacketCount.getAndIncrement());
            keyBuf.writeBytes(serverKey);

            hash.update(counterBuf);
            hash.update(buf);
            hash.update(keyBuf);
            byte[] digested = hash.digest();
            return Arrays.copyOf(digested, 8);
        } finally {
            counterBuf.release();
            keyBuf.release();
        }
    }

    public boolean isEncrypted() {
        return encryptionCipher != null;
    }

    private void close() {
        if (!connection.isClosed()) {
            connection.close();
        }

        server.getSessionManager().remove(this);

        // Free native resources if required
        if (encryptionCipher != null) {
            encryptionCipher.free();
        }
        if (decryptionCipher != null) {
            decryptionCipher.free();
        }

        // Make sure the entity is no longer being ticked
        if (playerSession != null) {
            playerSession.removeInternal();
        }
    }

    public BungeeCipher getEncryptionCipher() {
        return encryptionCipher;
    }

    public BungeeCipher getDecryptionCipher() {
        return decryptionCipher;
    }

    public PlayerSession getPlayerSession() {
        return playerSession;
    }

    public PlayerSession initializePlayerSession(NukkitLevel level) {
        checkForClosed();
        Preconditions.checkState(playerSession == null, "PlayerOld session already initialized");

        PlayerInitializationEvent event = new PlayerInitializationEvent(this, level);

        if (event.getPlayerSession() != null) {
            playerSession = event.getPlayerSession();
        } else {
            playerSession = new PlayerSession(this, level);
        }
        if (!server.getSessionManager().add(this)) {
            throw new IllegalStateException("Player could not be added to SessionManager");
        }
        return playerSession;
    }

    public ClientData getClientData() {
        return clientData;
    }

    public void setClientData(ClientData clientData) {
        this.clientData = clientData;
    }

    public int getProtocolVersion() {
        return protocolVersion;
    }

    void setProtocolVersion(int protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public void disconnect() {
        disconnect(null, true);
    }

    public void disconnect(@Nullable String reason) {
        disconnect(reason, false);
    }

    public void disconnect(@Nullable String reason, boolean hideReason) {
        checkForClosed();

        DisconnectPacket packet = new DisconnectPacket();
        if (reason == null || hideReason) {
            packet.setDisconnectScreenHidden(true);
            reason = "disconnect.disconnected";
        } else {
            packet.setKickMessage(reason);
        }
        sendImmediatePackage(packet);

        if (authData != null) {
            log.info("{} ({}) has been disconnected from the server: {}", authData.getDisplayName(),
                    getRemoteAddress().map(Object::toString).orElse("UNKNOWN"), server.getLocaleManager().replaceI18n(reason));
        } else {
            log.info("{} has lost connection to the server: {}", getRemoteAddress().map(Object::toString).orElse("UNKNOWN"),
                    reason);
        }

        close();
    }

    @Override
    public void onTimeout() {
        if (authData != null) {
            log.info("{} ({}) has been disconnected from the server: {}", authData.getDisplayName(),
                    getRemoteAddress().map(Object::toString).orElse("UNKNOWN"), server.getLocaleManager().replaceI18n("disconnect.timeout"));
        } else {
            log.info("{} has lost connection to the server: {}", getRemoteAddress().map(Object::toString).orElse("UNKNOWN"),
                    "disconnect.timeout");
        }

        close();
    }

    public void onWrappedPacket(WrappedPacket packet) throws Exception {
        Preconditions.checkNotNull(packet, "packet");
        if (wrapperHandler == null) {
            return;
        }

        ByteBuf wrappedData = packet.getPayload();
        ByteBuf unwrappedData = null;
        try {
            if (isEncrypted()) {
                // Decryption
                unwrappedData = PooledByteBufAllocator.DEFAULT.directBuffer(wrappedData.readableBytes());
                decryptionCipher.cipher(wrappedData, unwrappedData);
                // TODO: Persuade Microjang into removing adler32
                unwrappedData = unwrappedData.slice(0, unwrappedData.readableBytes() - 8);
            } else {
                // Encryption not enabled so it should be readable.
                unwrappedData = wrappedData;
            }

            String to = getRemoteAddress().orElse(LOOPBACK_BEDROCK).toString();
            // Decompress and handle packets
            for (BedrockPacket pk : wrapperHandler.decompressPackets(packetCodec, unwrappedData)) {
                if (log.isTraceEnabled()) {
                    log.trace("Inbound {}: {}", to, pk.toString());
                }
                pk.handle(handler);
            }
        } finally {
            wrappedData.release();
            if (unwrappedData != null && unwrappedData != wrappedData) {
                unwrappedData.release();
            }
        }
    }

    public boolean isClosed() {
        return connection.isClosed();
    }

    public Optional<InetSocketAddress> getRemoteAddress() {
        return connection.getRemoteAddress();
    }

    public RakNetSession getConnection() {
        return connection;
    }

    public NukkitServer getServer() {
        return server;
    }
}
