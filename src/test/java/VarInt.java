import cn.nukkit.utils.BinaryStream;

import java.math.BigInteger;

public class VarInt {

    private static final BigInteger UNSIGNED_LONG_MAX_VALUE = new BigInteger("FFFFFFFFFFFFFFFF", 16);

    private static void _assert(BigInteger integer) {
        if (integer == null) {
            throw new IllegalArgumentException("The value should not be null");
        }

        if (integer.compareTo(UNSIGNED_LONG_MAX_VALUE) > 0) {
            throw new IllegalArgumentException("The value is too big");
        }
    }

    /**
     * @param v Signed int
     * @return Unsigned encoded int
     */
    public static long encodeZigZag32(int v) {
        // Note:  the right-shift must be arithmetic
        return (long) ((v << 1) ^ (v >> 31));
    }

    /**
     * @param v Unsigned encoded int
     * @return Signed decoded int
     */
    public static int decodeZigZag32(long v) {
        return (int) (v >> 1) ^ -(int) (v & 1);
    }

    /**
     * @param v Signed long
     * @return Unsigned encoded long
     */
    public static BigInteger encodeZigZag64(long v) {
        BigInteger origin = BigInteger.valueOf(v);
        BigInteger left = origin.shiftLeft(1);
        BigInteger right = origin.shiftRight(63);
        return left.xor(right);
    }

    /**
     * @param v Signed encoded long
     * @return Unsigned decoded long
     */
    public static BigInteger decodeZigZag64(long v) {
        return decodeZigZag64(BigInteger.valueOf(v).and(UNSIGNED_LONG_MAX_VALUE));
    }

    /**
     * @param v Unsigned encoded long
     * @return Unsigned decoded long
     */
    public static BigInteger decodeZigZag64(BigInteger v) {
        _assert(v);
        BigInteger left = v.shiftRight(1);
        BigInteger right = v.and(BigInteger.ONE).negate();
        return left.xor(right);
    }

    private static BigInteger _readVarInt(BinaryStream stream, int maxSize) {
        BigInteger result = BigInteger.ZERO;
        int offset = 0;
        int b;

        do {
            if (offset >= maxSize) {
                throw new IllegalArgumentException("VarInt too big");
            }

            b = stream.getByte();
            result = result.or(BigInteger.valueOf((b & 0x7f) << (offset * 7)));
            offset++;
        } while ((b & 0x80) > 0);

        return result;
    }

    /**
     * @param stream
     * @return Signed int
     */
    public static int readVarInt(BinaryStream stream) {
        return decodeZigZag32(readUnsignedVarInt(stream));
    }

    /**
     * @param stream
     * @return Unsigned int
     */
    public static long readUnsignedVarInt(BinaryStream stream) {
        return _readVarInt(stream, 5).longValue();
    }

    /**
     * @param stream
     * @return Signed long
     */
    public static long readVarLong(BinaryStream stream) {
        return decodeZigZag64(readUnsignedVarInt(stream)).longValue();
    }

    /**
     * @param stream
     * @return Unsigned long
     */
    public static BigInteger readUnsignedVarLong(BinaryStream stream) {
        return _readVarInt(stream, 10);
    }

    private static void _writeVarInt(BinaryStream stream, BigInteger v) {
        _assert(v);
        v = v.and(UNSIGNED_LONG_MAX_VALUE);
        BigInteger i = BigInteger.valueOf(-128);
        BigInteger BIX7F = BigInteger.valueOf(0x7f);
        BigInteger BIX80 = BigInteger.valueOf(0x80);
        while (!v.and(i).equals(BigInteger.ZERO)) {
            stream.putByte(v.and(BIX7F).or(BIX80).byteValue());
            v = v.shiftRight(7);
        }

        stream.putByte(v.byteValue());
    }

    /**
     * @param stream
     * @param value  Signed int
     */
    public static void writeVarInt(BinaryStream stream, int value) {
        writeUnsignedVarInt(stream, encodeZigZag32(value));
    }

    /**
     * @param stream
     * @param value  Unsigned int
     */
    public static void writeUnsignedVarInt(BinaryStream stream, long value) {
        _writeVarInt(stream, BigInteger.valueOf(value));
    }

    /**
     * @param stream
     * @param value  Signed long
     */
    public static void writeVarLong(BinaryStream stream, long value) {
        writeUnsignedVarLong(stream, encodeZigZag64(value));
    }

    /**
     * @param stream
     * @param value  Unsigned long
     */
    public static void writeUnsignedVarLong(BinaryStream stream, BigInteger value) {
        _assert(value);
        _writeVarInt(stream, value);
    }
}
