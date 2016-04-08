package cn.nukkit.nbt.stream;

import java.io.*;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class NBTOutputStream implements DataOutput, AutoCloseable {
    private final DataOutputStream stream;
    private final ByteOrder endianness;

    public NBTOutputStream(OutputStream stream) {
        this(stream, ByteOrder.BIG_ENDIAN);
    }

    public NBTOutputStream(OutputStream stream, ByteOrder endianness) {
        this.stream = stream instanceof DataOutputStream ? (DataOutputStream)stream : new DataOutputStream(stream);
        this.endianness = endianness;
    }

    public ByteOrder getEndianness() {
        return endianness;
    }

    @Override
    public void write(byte[] bytes) throws IOException {
        this.stream.write(bytes);
    }

    @Override
    public void write(byte b[], int off, int len) throws IOException {
        this.stream.write(b, off, len);
    }

    @Override
    public void write(int b) throws IOException {
        this.stream.write(b);
    }

    @Override
    public void writeBoolean(boolean v) throws IOException {
        this.stream.writeBoolean(v);
    }

    @Override
    public void writeByte(int v) throws IOException {
        this.stream.writeByte(v);
    }

    @Override
    public void writeShort(int v) throws IOException {
        if (endianness == ByteOrder.LITTLE_ENDIAN) {
            v = Integer.reverseBytes(v) >> 16;
        }
        this.stream.writeShort(v);
    }

    @Override
    public void writeChar(int v) throws IOException {
        if (endianness == ByteOrder.LITTLE_ENDIAN) {
            v = Character.reverseBytes((char) v);
        }
        this.stream.writeChar(v);
    }

    @Override
    public void writeInt(int v) throws IOException {
        if (endianness == ByteOrder.LITTLE_ENDIAN) {
            v = Integer.reverseBytes(v);
        }
        this.stream.writeInt(v);
    }

    @Override
    public void writeLong(long v) throws IOException {
        if (endianness == ByteOrder.LITTLE_ENDIAN) {
            v = Long.reverseBytes(v);
        }
        this.stream.writeLong(v);
    }

    @Override
    public void writeFloat(float v) throws IOException {
        this.writeInt(Float.floatToIntBits(v));
    }

    @Override
    public void writeDouble(double v) throws IOException {
        this.writeLong(Double.doubleToLongBits(v));
    }

    @Override
    public void writeBytes(String s) throws IOException {
        this.stream.writeBytes(s);
    }

    @Override
    public void writeChars(String s) throws IOException {
        this.stream.writeChars(s);
    }

    @Override
    public void writeUTF(String s) throws IOException {
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        this.writeShort(bytes.length);
        this.stream.write(bytes);
    }

    @Override
    public void close() throws IOException {
        this.stream.close();
    }
}
