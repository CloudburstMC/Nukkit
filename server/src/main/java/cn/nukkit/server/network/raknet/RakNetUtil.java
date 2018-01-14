package cn.nukkit.server.network.raknet;

import io.netty.buffer.ByteBuf;
import lombok.experimental.UtilityClass;

import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@UtilityClass
public class RakNetUtil {
    public static final byte[] RAKNET_UNCONNECTED_MAGIC = new byte[]{
            0, -1, -1, 0, -2, -2, -2, -2, -3, -3, -3, -3, 18, 52, 86, 120
    };
    public static final byte RAKNET_PROTOCOL_VERSION = 8; // Mojangs version.
    public static final short MINIMUM_MTU_SIZE = 400;
    public static final short MAXIMUM_MTU_SIZE = 1492;
    public static final int MAX_ENCAPSULATED_HEADER_SIZE = 9;
    public static final int MAX_MESSAGE_HEADER_SIZE = 23;
    public static final int AF_INET6 = 23;

    public static String readString(ByteBuf buffer) {
        byte[] stringBytes = new byte[buffer.readShort()];
        buffer.readBytes(stringBytes);
        return new String(stringBytes, StandardCharsets.UTF_8);
    }

    public static void writeString(ByteBuf buffer, String string) {
        byte[] stringBytes = string.getBytes(StandardCharsets.UTF_8);
        buffer.writeShort(stringBytes.length);
        buffer.writeBytes(stringBytes);
    }

    public static InetSocketAddress readAddress(ByteBuf buffer) {
        short type = buffer.readUnsignedByte();
        byte[] address;
        int port;
        if (type == 4) {
            address = new byte[4];
            buffer.readBytes(address);
            port = buffer.readUnsignedShort();
        } else if (type == 6) {
            buffer.skipBytes(2); // Family, AF_INET6
            port = buffer.readUnsignedShort();
            buffer.skipBytes(8); // Flow information
            address = new byte[16];
            buffer.readBytes(address);
            buffer.readInt(); // Scope ID
        } else {
            throw new UnsupportedOperationException("Unknown Internet Protocol version.");
        }

        try {
            return new InetSocketAddress(InetAddress.getByAddress(address), port);
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static void writeAddress(ByteBuf buffer, InetSocketAddress address) {
        if (address.getAddress() instanceof Inet4Address) {
            buffer.writeByte((4 & 0xFF));
            buffer.writeBytes(address.getAddress().getAddress());
            buffer.writeShort(address.getPort());
        } else if (address.getAddress() instanceof Inet6Address) {
            buffer.writeByte((6 & 0xFF));
            buffer.writeShortLE(AF_INET6);
            buffer.writeShort(address.getPort());
            buffer.writeInt(0);
            buffer.writeBytes(address.getAddress().getAddress());
            buffer.writeInt(0);
        } else {
            throw new UnsupportedOperationException("Unknown InetAddress instance");
        }
    }

    public static void verifyUnconnectedMagic(ByteBuf buffer) {
        byte[] readMagic = new byte[RAKNET_UNCONNECTED_MAGIC.length];
        buffer.readBytes(readMagic);

        if (!Arrays.equals(readMagic, RAKNET_UNCONNECTED_MAGIC)) {
            throw new RuntimeException("Invalid packet magic.");
        }
    }

    public static void writeUnconnectedMagic(ByteBuf buffer) {
        buffer.writeBytes(RAKNET_UNCONNECTED_MAGIC);
    }
}
