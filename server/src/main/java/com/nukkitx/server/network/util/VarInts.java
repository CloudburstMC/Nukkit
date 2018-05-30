package com.nukkitx.server.network.util;

import io.netty.buffer.ByteBuf;
import lombok.experimental.UtilityClass;

@UtilityClass
public class VarInts {
    public static void writeInt(ByteBuf buffer, int integer) {
        encodeUnsigned(buffer, (integer << 1) ^ (integer >> 31));
    }

    public static int readInt(ByteBuf buffer) {
        int n = (int) decodeUnsigned(buffer);
        return (n >>> 1) ^ -(n & 1);
    }

    public static void writeUnsignedInt(ByteBuf buffer, long integer) {
        encodeUnsigned(buffer, integer);
    }

    public static int readUnsignedInt(ByteBuf buffer) {
        return (int) decodeUnsigned(buffer);
    }

    public static void writeLong(ByteBuf buffer, long longInteger) {
        encodeUnsigned(buffer, (longInteger << 1) ^ (longInteger >> 63));
    }

    public static long readLong(ByteBuf buffer) {
        long n = decodeUnsigned(buffer);
        return (n >>> 1) ^ -(n & 1);
    }

    public static void writeUnsignedLong(ByteBuf buffer, long longInteger) {
        encodeUnsigned(buffer, longInteger);
    }

    public static long readUnsignedLong(ByteBuf buffer) {
        return decodeUnsigned(buffer);
    }

    private static long decodeUnsigned(ByteBuf buffer) {
        long result = 0;
        for (int shift = 0; shift < 64; shift += 7) {
            final byte b = buffer.readByte();
            result |= (long) (b & 0x7F) << shift;
            if ((b & 0x80) == 0) {
                return result;
            }
        }
        throw new ArithmeticException("Varint was too large");
    }

    private static void encodeUnsigned(ByteBuf buffer, long value) {
        while (true) {
            if ((value & ~0x7FL) == 0) {
                buffer.writeByte((int) value);
                return;
            } else {
                buffer.writeByte((byte) (((int) value & 0x7F) | 0x80));
                value >>>= 7;
            }
        }
    }
}
