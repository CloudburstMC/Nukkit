package cn.nukkit.network.query;

import cn.nukkit.Server;
import cn.nukkit.event.server.QueryRegenerateEvent;
import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class QueryHandler {

    public static final byte HANDSHAKE = 0x09;
    public static final byte STATISTICS = 0x00;

    private final Server server;
    private byte[] lastToken;
    private byte[] token;
    private byte[] longData;
    private byte[] shortData;
    private long timeout;

    public QueryHandler() {
        this.server = Server.getInstance();
        this.server.getLogger().info(this.server.getLanguage().translateString("nukkit.server.query.start"));
        String ip = this.server.getIp();
        String addr = (!ip.isEmpty()) ? ip : "0.0.0.0";
        int port = this.server.getPort();
        this.server.getLogger().info(this.server.getLanguage().translateString("nukkit.server.query.info", String.valueOf(port)));

        this.regenerateToken();
        this.lastToken = this.token;
        this.regenerateInfo();
        this.server.getLogger().info(this.server.getLanguage().translateString("nukkit.server.query.running", new String[]{addr, String.valueOf(port)}));
    }

    public void regenerateInfo() {
        QueryRegenerateEvent ev = this.server.getQueryInformation();
        this.longData = ev.getLongQuery(this.longData);
        this.shortData = ev.getShortQuery(this.shortData);
        this.timeout = System.currentTimeMillis() + ev.getTimeout();
    }

    public void regenerateToken() {
        this.lastToken = this.token;
        byte[] token = new byte[16];
        for (int i = 0; i < 16; i++) {
            token[i] = (byte) new Random().nextInt(255);
        }
        this.token = token;
    }

    public static String getTokenString(byte[] token, InetAddress address) {
        return getTokenString(new String(token), address);
    }

    public static String getTokenString(String token, InetAddress address) {
        String salt = address.toString();
        try {
            return String.valueOf(Binary.readInt(Binary.subBytes(MessageDigest.getInstance("SHA-512").digest((salt + ":" + token).getBytes()), 7, 4)));
        } catch (NoSuchAlgorithmException e) {
            return String.valueOf(new Random().nextInt());
        }
    }

    public void handle(InetSocketAddress address, ByteBuf packet) {
        short packetId = packet.readUnsignedByte();
        int sessionId = packet.readInt();

        switch (packetId) {
            case HANDSHAKE:
                ByteBuf reply = PooledByteBufAllocator.DEFAULT.directBuffer();
                reply.writeByte(HANDSHAKE);
                reply.writeInt(sessionId);
                reply.writeBytes(getTokenString(this.token, address.getAddress()).getBytes(StandardCharsets.UTF_8));
                reply.writeByte(0);

                this.server.getNetwork().sendPacket(address, reply);
                break;
            case STATISTICS:
                String token = String.valueOf(packet.readInt());
                if (!token.equals(getTokenString(this.token, address.getAddress())) && !token.equals(getTokenString(this.lastToken, address.getAddress()))) {
                    break;
                }

                if (this.timeout < System.currentTimeMillis()) {
                    this.regenerateInfo();
                }
                reply = PooledByteBufAllocator.DEFAULT.directBuffer();
                reply.writeByte(STATISTICS);
                reply.writeInt(sessionId);
                if (packet.readableBytes() == 8) {
                    reply.writeBytes(this.longData);
                } else {
                    reply.writeBytes(this.shortData);
                }

                this.server.getNetwork().sendPacket(address, reply);
                break;
        }
    }
}
