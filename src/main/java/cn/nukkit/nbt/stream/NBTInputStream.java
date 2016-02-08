package cn.nukkit.nbt.stream;

import java.io.*;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class NBTInputStream extends FilterInputStream implements DataInput {
    private final ByteOrder endianness;

    public NBTInputStream(InputStream stream) {
        this(stream, ByteOrder.BIG_ENDIAN);
    }

    public NBTInputStream(InputStream stream, ByteOrder endianness) {
        super(stream instanceof DataInputStream ? stream : new DataInputStream(stream));
        this.endianness = endianness;
    }

    public ByteOrder getEndianness() {
        return endianness;
    }

    protected DataInputStream getStream() {
        return (DataInputStream) super.in;
    }

    @Override
    public void readFully(byte[] b) throws IOException {
        this.getStream().readFully(b);
    }

    @Override
    public void readFully(byte[] b, int off, int len) throws IOException {
        this.getStream().readFully(b, off, len);
    }

    @Override
    public int skipBytes(int n) throws IOException {
        return this.getStream().skipBytes(n);
    }

    @Override
    public boolean readBoolean() throws IOException {
        return this.getStream().readBoolean();
    }

    @Override
    public byte readByte() throws IOException {
        return this.getStream().readByte();
    }

    @Override
    public int readUnsignedByte() throws IOException {
        return this.getStream().readUnsignedByte();
    }

    @Override
    public short readShort() throws IOException {
        short s = this.getStream().readShort();
        if (endianness == ByteOrder.LITTLE_ENDIAN) {
            s = Short.reverseBytes(s);
        }
        return s;
    }

    @Override
    public int readUnsignedShort() throws IOException {
        int s = this.getStream().readUnsignedShort();
        if (endianness == ByteOrder.LITTLE_ENDIAN) {
            s = Integer.reverseBytes(s) >> 16;
        }
        return s;
    }

    @Override
    public char readChar() throws IOException {
        char c = this.getStream().readChar();
        if (endianness == ByteOrder.LITTLE_ENDIAN) {
            c = Character.reverseBytes(c);
        }
        return c;
    }

    @Override
    public int readInt() throws IOException {
        int i = this.getStream().readInt();
        if (endianness == ByteOrder.LITTLE_ENDIAN) {
            i = Integer.reverseBytes(i);
        }
        return i;
    }

    @Override
    public long readLong() throws IOException {
        long l = this.getStream().readLong();
        if (endianness == ByteOrder.LITTLE_ENDIAN) {
            l = Long.reverseBytes(l);
        }
        return l;
    }

    @Override
    public float readFloat() throws IOException {
        return Float.intBitsToFloat(this.readInt());
    }

    @Override
    public double readDouble() throws IOException {
        return Double.longBitsToDouble(this.readLong());
    }

    @Override
    @Deprecated
    public String readLine() throws IOException {
        return this.getStream().readLine();
    }

    @Override
    public String readUTF() throws IOException {
        int length = this.readUnsignedShort();
        byte[] bytes = new byte[length];
        this.read(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    @Override
    public void close() throws IOException {
        this.getStream().close();
    }
}
