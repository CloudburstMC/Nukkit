package cn.nukkit.network.query;

import cn.nukkit.Server;
import cn.nukkit.event.server.QueryRegenerateEvent;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import lombok.extern.log4j.Log4j2;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@Log4j2
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
        log.info(this.server.getLanguage().translateString("nukkit.server.query.start"));
        String ip = this.server.getIp();
        String addr = (!ip.isEmpty()) ? ip : "0.0.0.0";
        int port = this.server.getPort();
        log.info(this.server.getLanguage().translateString("nukkit.server.query.info", String.valueOf(port)));

        this.regenerateToken();
        this.lastToken = this.token;
        this.regenerateInfo();
        log.info(this.server.getLanguage().translateString("nukkit.server.query.running", new String[]{addr, String.valueOf(port)}));
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

    public static byte[] getTokenString(String token, InetAddress address) {
        return getTokenString(token.getBytes(StandardCharsets.UTF_8), address);
    }

    public static byte[] getTokenString(byte[] token, InetAddress address) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(address.toString().getBytes(StandardCharsets.UTF_8));
            digest.update(token);
            return Arrays.copyOf(digest.digest(), 4);
        } catch (NoSuchAlgorithmException e) {
            return ByteBuffer.allocate(4).putInt(ThreadLocalRandom.current().nextInt()).array();
        }
    }

    public void handle(InetSocketAddress address, ByteBuf packet) {
        short packetId = packet.readUnsignedByte();
        int sessionId = packet.readInt();

        switch (packetId) {
            case HANDSHAKE:
                ByteBuf reply = ByteBufAllocator.DEFAULT.ioBuffer(10); // 1 + 4 + 4 + 1
                reply.writeByte(HANDSHAKE);
                reply.writeInt(sessionId);
                reply.writeBytes(getTokenString(this.token, address.getAddress()));
                reply.writeByte(0);

                this.server.getNetwork().sendPacket(address, reply);
                break;
            case STATISTICS:
                byte[] token = new byte[4];
                packet.readBytes(token);

                if (!Arrays.equals(token, getTokenString(this.token, address.getAddress())) &&
                        !Arrays.equals(token, getTokenString(this.lastToken, address.getAddress()))) {
                    break;
                }

                if (this.timeout < System.currentTimeMillis()) {
                    this.regenerateInfo();
                }
                reply = ByteBufAllocator.DEFAULT.ioBuffer(64);
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
