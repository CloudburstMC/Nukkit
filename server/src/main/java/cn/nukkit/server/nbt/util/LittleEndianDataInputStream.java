package cn.nukkit.server.nbt.util;

import javax.annotation.Nonnull;
import java.io.*;

public class LittleEndianDataInputStream implements DataInput, Closeable {
    private final DataInputStream stream;

    public LittleEndianDataInputStream(InputStream stream) {
        this.stream = new DataInputStream(stream);
    }

    public LittleEndianDataInputStream(DataInputStream stream) {
        this.stream = stream;
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }

    @Override
    public void readFully(@Nonnull byte[] bytes) throws IOException {
        stream.readFully(bytes);
    }

    @Override
    public void readFully(@Nonnull byte[] bytes, int offset, int length) throws IOException {
        stream.readFully(bytes, offset, length);
    }

    @Override
    public int skipBytes(int amount) throws IOException {
        return stream.skipBytes(amount);
    }

    @Override
    public boolean readBoolean() throws IOException {
        return stream.readBoolean();
    }

    @Override
    public byte readByte() throws IOException {
        return stream.readByte();
    }

    @Override
    public int readUnsignedByte() throws IOException {
        return stream.readUnsignedByte();
    }

    @Override
    public short readShort() throws IOException {
        return Short.reverseBytes(stream.readShort());
    }

    @Override
    public int readUnsignedShort() throws IOException {
        return Integer.reverseBytes(stream.readUnsignedShort());
    }

    @Override
    public char readChar() throws IOException {
        return Character.reverseBytes(stream.readChar());
    }

    @Override
    public int readInt() throws IOException {
        return Integer.reverseBytes(stream.readInt());
    }

    @Override
    public long readLong() throws IOException {
        return Long.reverseBytes(stream.readLong());
    }

    @Override
    public float readFloat() throws IOException {
        return Float.intBitsToFloat(Integer.reverseBytes(stream.readInt()));
    }

    @Override
    public double readDouble() throws IOException {
        return Double.longBitsToDouble(Long.reverseBytes(stream.readLong()));
    }

    @Override
    @Deprecated
    public String readLine() throws IOException {
        return stream.readLine();
    }

    @Override
    public String readUTF() throws IOException {
        return stream.readUTF();
    }
}
