package com.nukkitx.server.network.minecraft.session;

import com.google.common.base.Preconditions;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.event.player.PlayerInitializationEvent;
import com.nukkitx.server.level.NukkitLevel;
import com.nukkitx.server.network.minecraft.MinecraftPacket;
import com.nukkitx.server.network.minecraft.MinecraftPacketRegistry;
import com.nukkitx.server.network.minecraft.NetworkPacketHandler;
import com.nukkitx.server.network.minecraft.annotations.NoEncryption;
import com.nukkitx.server.network.minecraft.packet.DisconnectPacket;
import com.nukkitx.server.network.minecraft.packet.WrappedPacket;
import com.nukkitx.server.network.minecraft.session.data.AuthData;
import com.nukkitx.server.network.minecraft.session.data.ClientData;
import com.nukkitx.server.network.minecraft.wrapper.DefaultWrapperHandler;
import com.nukkitx.server.network.minecraft.wrapper.WrapperHandler;
import com.nukkitx.server.network.raknet.NetworkPacket;
import com.nukkitx.server.network.raknet.RakNetPacket;
import com.nukkitx.server.network.raknet.RakNetPacketRegistry;
import com.nukkitx.server.network.raknet.session.RakNetSession;
import com.nukkitx.server.network.raknet.session.SessionConnection;
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
public class MinecraftSession {
    private static final ThreadLocal<VoxelwindHash> hashLocal = ThreadLocal.withInitial(NativeCodeFactory.hash::newInstance);
    private static final InetSocketAddress LOOPBACK_MINECRAFT = new InetSocketAddress(InetAddress.getLoopbackAddress(), 19132);
    private static final int TIMEOUT_MS = 30000;
    private final AtomicLong lastKnownUpdate = new AtomicLong(System.currentTimeMillis());
    private final Queue<MinecraftPacket> currentlyQueued = new ConcurrentLinkedQueue<>();
    private final AtomicLong sentEncryptedPacketCount = new AtomicLong();
    private final NukkitServer server;
    private NetworkPacketHandler handler;
    private WrapperHandler wrapperCompressionHandler = DefaultWrapperHandler.DEFAULT;
    private SessionConnection connection;
    private AuthData authData;
    private ClientData clientData;
    private BungeeCipher encryptionCipher;
    private BungeeCipher decryptionCipher;
    private PlayerSession playerSession;
    private byte[] serverKey;
    private int protocolVersion;

    public MinecraftSession(NetworkPacketHandler handler, NukkitServer server, SessionConnection connection) {
        this.server = server;
        this.handler = handler;
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

    public WrapperHandler getWrapperHandler() {
        return wrapperCompressionHandler;
    }

    public void setWrapperHandler(WrapperHandler wrapperCompressionHandler) {
        checkForClosed();
        Preconditions.checkNotNull(handler, "wrapperCompressionHandler");
        this.wrapperCompressionHandler = wrapperCompressionHandler;
    }

    private void checkForClosed() {
        Preconditions.checkState(!connection.isClosed(), "Connection has been closed!");
    }

    public void addToSendQueue(MinecraftPacket packet) {
        checkForClosed();
        Preconditions.checkNotNull(packet, "packet");

        // Verify that the packet ID exists.
        MinecraftPacketRegistry.getId(packet);

        currentlyQueued.add(packet);
    }

    public void sendImmediatePackage(NetworkPacket packet) {
        checkForClosed();
        Preconditions.checkNotNull(packet, "packet");
        internalSendPackage(packet);
    }

    private void internalSendPackage(NetworkPacket packet) {
        if (log.isTraceEnabled()) {
            String to = connection.getRemoteAddress().orElse(LOOPBACK_MINECRAFT).toString();
            log.trace("Outbound {}: {}", to, packet);
        }

        ByteBuf dataToSend;
        if (packet instanceof MinecraftPacket || packet instanceof WrappedPacket) {
            dataToSend = PooledByteBufAllocator.DEFAULT.directBuffer();
            dataToSend.writeByte((0xFE & 0xFF));

            ByteBuf compressed;
            if (packet instanceof WrappedPacket) {
                WrappedPacket wrapped = (WrappedPacket) packet;
                if (wrapped.getPayload() == null) {
                    compressed = wrapperCompressionHandler.compressPackets(wrapped.getPackets());
                } else {
                    compressed = wrapped.getPayload();
                }
            } else {
                compressed = wrapperCompressionHandler.compressPackets((MinecraftPacket) packet);
            }

            try {
                if (encryptionCipher == null || packet.getClass().isAnnotationPresent(NoEncryption.class)) {
                    compressed.readerIndex(0);
                    dataToSend.writeBytes(compressed);
                } else {
                    compressed.readerIndex(0);
                    byte[] trailer = generateTrailer(compressed);
                    compressed.writeBytes(trailer);

                    compressed.readerIndex(0);
                    encryptionCipher.cipher(compressed, dataToSend);
                }
            } catch (GeneralSecurityException e) {
                dataToSend.release();
                throw new RuntimeException("Unable to encipher package", e);
            } finally {
                compressed.release();
            }
        } else if (packet instanceof RakNetPacket) {
            dataToSend = RakNetPacketRegistry.encode((RakNetPacket) packet);
        } else {
            throw new IllegalStateException("Unknown packet type");
        }

        connection.sendPacket(dataToSend);
    }

    public void onTick() {
        if (isClosed()) {
            return;
        }

        if (isTimedOut()) {
            disconnect("disconnect.timeout");
            return;
        }

        connection.onTick();
        sendQueued();
    }

    private void sendQueued() {
        MinecraftPacket packet;
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

                continue;
            } else if (wrapper.getPackets().size() >= 10) {
                // Reached a per-batch limit on packages, send these packages now
                internalSendPackage(wrapper);
                wrapper = new WrappedPacket();

                try {
                    // Delay things a tiny bit
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    log.error("Interrupted", e);
                }
            }

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

        if (connection instanceof RakNetSession) {
            ((RakNetSession) connection).setUseOrdering(true);
        }
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

    public void close() {
        connection.close();

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

    public void setProtocolVersion(int protocolVersion) {
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

    public boolean isClosed() {
        return connection.isClosed();
    }

    public Optional<InetSocketAddress> getRemoteAddress() {
        return connection.getRemoteAddress();
    }

    private boolean isTimedOut() {
        return System.currentTimeMillis() - lastKnownUpdate.get() >= TIMEOUT_MS;
    }

    public void touch() {
        checkForClosed();
        lastKnownUpdate.set(System.currentTimeMillis());
    }

    public SessionConnection getConnection() {
        return connection;
    }

    public NukkitServer getServer() {
        return server;
    }
}
