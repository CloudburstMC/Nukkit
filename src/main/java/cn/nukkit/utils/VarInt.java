package cn.nukkit.utils;

public class VarInt {
    public static long encodeZigZag32(int n) {
        // Note:  the right-shift must be arithmetic
        return (long) ((n << 1) ^ (n >> 31));
    }

    public static int decodeZigZag32(long n) {
        return (int) (n >> 1) ^ -(int) (n & 1);
    }

    public static long encodeZigZag64(long n) {
        return ((n << 1) ^ (n >> 63));
    }

    public static long decodeZigZag64(long n) {
        return (n >> 1) ^ -(n & 1);
    }

    public static long readRawVarInt32(BinaryStream buf, int maxSize) {
        long result = 0;
        int j = 0;
        int b0;

        do {
            b0 = buf.getByte(); // -1 if EOS
            if (b0 < 0) throw new IllegalArgumentException("Not enough bytes for VarInt");

            result |= (long) (b0 & 0x7f) << j++ * 7;

            if (j > maxSize) {
                throw new IllegalArgumentException("VarInt too big");
            }
        } while ((b0 & 0x80) == 0x80);

        return result;
    }

    public static long readRawVarInt64(BinaryStream buf, int maxSize) {
        long result = 0;
        int j = 0;
        int b0;

        do {
            b0 = buf.getByte(); // -1 if EOS
            if (b0 < 0) throw new IllegalArgumentException("Not enough bytes for VarInt");

            result |= (long) (b0 & 0x7f) << j++ * 7;

            if (j > maxSize) {
                throw new IllegalArgumentException("VarInt too big");
            }
        } while ((b0 & 0x80) == 0x80);

        return result;
    }

    public static void writeRawVarInt32(BinaryStream buf, long value) {
        while ((value & -128) != 0) {
            buf.putByte((byte) ((value & 0x7F) | 0x80));
            value >>= 7;
        }

        buf.putByte((byte) value);
    }

    public static void writeRawVarInt64(BinaryStream buf, long value) {
        while ((value & 0xFFFFFFFFFFFFFF80L) != 0) {
            buf.putByte((byte) ((value & 0x7F) | 0x80));
            value >>= 7;
        }

        buf.putByte((byte) value);
    }

    // Int

    public static void writeInt32(BinaryStream stream, int value) {
        writeRawVarInt32(stream, (long) value);
    }

    public static int readInt32(BinaryStream stream) {
        return (int) readRawVarInt32(stream, 5);
    }

    public static void writeSInt32(BinaryStream stream, int value) {
        writeRawVarInt32(stream, encodeZigZag32(value));
    }

    public static int readSInt32(BinaryStream stream) {
        return decodeZigZag32(readRawVarInt32(stream, 5));
    }

    public static void writelong32(BinaryStream stream, long value) {
        writeRawVarInt32(stream, value);
    }

    public static long readlong32(BinaryStream stream) {
        return readRawVarInt32(stream, 5);
    }

    // Long

    public static void writeInt64(BinaryStream stream, long value) {
        writeRawVarInt64(stream, (long) value);
    }

    public static long readInt64(BinaryStream stream) {
        return (long) readRawVarInt64(stream, 10);
    }

    public static void writeSInt64(BinaryStream stream, long value) {
        writeRawVarInt64(stream, encodeZigZag64(value));
    }

    public static long readSInt64(BinaryStream stream) {
        return decodeZigZag64(readRawVarInt64(stream, 10));
    }

    public static void writelong64(BinaryStream stream, long value) {
        writeRawVarInt64(stream, value);
    }

    public static long readlong64(BinaryStream stream) {
        return readRawVarInt64(stream, 10);
    }
}
