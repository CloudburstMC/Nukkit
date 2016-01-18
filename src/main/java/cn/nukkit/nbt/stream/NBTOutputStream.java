package cn.nukkit.nbt.stream;

import java.io.*;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class NBTOutputStream extends FilterOutputStream implements DataOutput {
    private final ByteOrder endianness;

    public NBTOutputStream(OutputStream stream) {
        this(stream, ByteOrder.BIG_ENDIAN);
    }

    public NBTOutputStream(OutputStream stream, ByteOrder endianness) {
        super(stream instanceof DataOutputStream ? stream : new DataOutputStream(stream));
        this.endianness = endianness;
    }

    public ByteOrder getEndianness() {
        return endianness;
    }

    protected DataOutputStream getStream() {
        return (DataOutputStream) super.out;
    }

    @Override
    public void writeBoolean(boolean v) throws IOException {
        this.getStream().writeBoolean(v);
    }

    @Override
    public void writeByte(int v) throws IOException {
        this.getStream().writeByte(v);
    }

    @Override
    public void writeShort(int v) throws IOException {
        if (endianness == ByteOrder.LITTLE_ENDIAN) {
            v = Integer.reverseBytes(v) >> 16;
        }
        this.getStream().writeShort(v);
    }

    @Override
    public void writeChar(int v) throws IOException {
        if (endianness == ByteOrder.LITTLE_ENDIAN) {
            v = Character.reverseBytes((char) v);
        }
        this.getStream().writeChar(v);
    }

    @Override
    public void writeInt(int v) throws IOException {
        if (endianness == ByteOrder.LITTLE_ENDIAN) {
            v = Integer.reverseBytes(v);
        }
        this.getStream().writeInt(v);
    }

    @Override
    public void writeLong(long v) throws IOException {
        if (endianness == ByteOrder.LITTLE_ENDIAN) {
            v = Long.reverseBytes(v);
        }
        this.getStream().writeLong(v);
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
        this.getStream().writeBytes(s);
    }

    @Override
    public void writeChars(String s) throws IOException {
        this.getStream().writeChars(s);
    }

    @Override
    public void writeUTF(String s) throws IOException {
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        this.writeShort(bytes.length);
        this.getStream().write(bytes);
    }

    @Override
    public void close() throws IOException {
        this.getStream().close();
    }
}
