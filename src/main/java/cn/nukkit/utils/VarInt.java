package cn.nukkit.utils;

import cn.nukkit.api.API;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static cn.nukkit.api.API.Definition.UNIVERSAL;
import static cn.nukkit.api.API.Usage.EXPERIMENTAL;

/**
 * Tool class for VarInt or VarLong operations.
 * <p>
 * Some code from http://wiki.vg/Protocol.
 *
 * @author MagicDroidX
 * @author lmlstarqaq
 */
@API(usage = EXPERIMENTAL, definition = UNIVERSAL)
public final class VarInt {

    private VarInt() {
        //no instance
    }

    /**
     * @param v Signed int
     * @return Unsigned encoded int
     */
    public static long encodeZigZag32(int v) {
        // Note:  the right-shift must be arithmetic
        return ((v << 1) ^ (v >> 31)) & 0xFFFFFFFFL;
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
    public static long encodeZigZag64(long v) {
        return (v << 1) ^ (v >> 63);
    }

    /**
     * @param v Signed encoded long
     * @return Unsigned decoded long
     */
    public static long decodeZigZag64(long v) {
        return (v >>> 1) ^ -(v & 1);
    }

    private static long read(ByteBuf buffer, int maxSize) {
        long value = 0;
        int size = 0;
        int b;
        while (((b = buffer.readByte()) & 0x80) == 0x80) {
            value |= (long) (b & 0x7F) << (size++ * 7);
            if (size >= maxSize) {
                throw new IllegalArgumentException("VarLong too big");
            }
        }

        return value | ((long) (b & 0x7F) << (size * 7));
    }

    private static long read(InputStream buffer, int maxSize) throws IOException {
        long value = 0;
        int size = 0;
        int b;
        while (((b = buffer.read()) & 0x80) == 0x80) {
            value |= (long) (b & 0x7F) << (size++ * 7);
            if (size >= maxSize) {
                throw new IllegalArgumentException("VarLong too big");
            }
        }

        return value | ((long) (b & 0x7F) << (size * 7));
    }

    /**
     * @param buffer BinaryStream
     * @return Signed int
     */
    public static int readVarInt(ByteBuf buffer) {
        return decodeZigZag32(readUnsignedVarInt(buffer));
    }

    /**
     * @param buffer InputStream
     * @return Signed int
     */
    public static int readVarInt(InputStream buffer) throws IOException {
        return decodeZigZag32(readUnsignedVarInt(buffer));
    }

    /**
     * @param buffer BinaryStream
     * @return Unsigned int
     */
    public static long readUnsignedVarInt(ByteBuf buffer) {
        return read(buffer, 5);
    }

    /**
     * @param buffer InputStream
     * @return Unsigned int
     */
    public static long readUnsignedVarInt(InputStream buffer) throws IOException {
        return read(buffer, 5);
    }

    /**
     * @param buffer BinaryStream
     * @return Signed long
     */
    public static long readVarLong(ByteBuf buffer) {
        return decodeZigZag64(readUnsignedVarLong(buffer));
    }

    /**
     * @param buffer InputStream
     * @return Signed long
     */
    public static long readVarLong(InputStream buffer) throws IOException {
        return decodeZigZag64(readUnsignedVarLong(buffer));
    }

    /**
     * @param buffer BinaryStream
     * @return Unsigned long
     */
    public static long readUnsignedVarLong(ByteBuf buffer) {
        return read(buffer, 10);
    }

    /**
     * @param buffer InputStream
     * @return Unsigned long
     */
    public static long readUnsignedVarLong(InputStream buffer) throws IOException {
        return read(buffer, 10);
    }

    private static void write(ByteBuf buffer, long value) {
        do {
            byte temp = (byte) (value & 0b01111111);
            // Note: >>> means that the sign bit is shifted with the rest of the number rather than being left alone
            value >>>= 7;
            if (value != 0) {
                temp |= 0b10000000;
            }
            buffer.writeByte(temp);
        } while (value != 0);
    }

    private static void write(OutputStream buffer, long value) throws IOException {
        do {
            byte temp = (byte) (value & 0b01111111);
            // Note: >>> means that the sign bit is shifted with the rest of the number rather than being left alone
            value >>>= 7;
            if (value != 0) {
                temp |= 0b10000000;
            }
            buffer.write(temp);
        } while (value != 0);
    }

    /**
     * @param buffer BinaryStream
     * @param value  Signed int
     */
    public static void writeVarInt(ByteBuf buffer, int value) {
        writeUnsignedVarInt(buffer, encodeZigZag32(value));
    }

    /**
     * @param buffer OutputStream
     * @param value  Signed int
     */
    public static void writeVarInt(OutputStream buffer, int value) throws IOException {
        writeUnsignedVarInt(buffer, encodeZigZag32(value));
    }

    /**
     * @param buffer BinaryStream
     * @param value  Unsigned int
     */
    public static void writeUnsignedVarInt(ByteBuf buffer, long value) {
        write(buffer, value);
    }

    /**
     * @param buffer OutputStream
     * @param value  Unsigned int
     */
    public static void writeUnsignedVarInt(OutputStream buffer, long value) throws IOException {
        write(buffer, value);
    }

    /**
     * @param buffer BinaryStream
     * @param value  Signed long
     */
    public static void writeVarLong(ByteBuf buffer, long value) {
        writeUnsignedVarLong(buffer, encodeZigZag64(value));
    }

    /**
     * @param buffer OutputStream
     * @param value  Signed long
     */
    public static void writeVarLong(OutputStream buffer, long value) throws IOException {
        writeUnsignedVarLong(buffer, encodeZigZag64(value));
    }

    /**
     * @param buffer BinaryStream
     * @param value  Unsigned long
     */
    public static void writeUnsignedVarLong(ByteBuf buffer, long value) {
        write(buffer, value);
    }

    /**
     * @param buffer OutputStream
     * @param value  Unsigned long
     */
    public static void writeUnsignedVarLong(OutputStream buffer, long value) throws IOException {
        write(buffer, value);
    }
}