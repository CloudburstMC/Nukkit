package cn.nukkit.server.nbt.stream;

import java.io.*;

public class SwappedDataOutputStream implements DataOutput, Closeable {
    private final DataOutputStream stream;

    public SwappedDataOutputStream(OutputStream stream) {
        this.stream = new DataOutputStream(stream);
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }

    @Override
    public void write(int val) throws IOException {
        stream.write(val);
    }

    @Override
    public void write(byte[] bytes) throws IOException {
        stream.write(bytes);
    }

    @Override
    public void write(byte[] bytes, int offset, int length) throws IOException {
        stream.write(bytes, offset, length);
    }

    @Override
    public void writeBoolean(boolean val) throws IOException {
        stream.writeBoolean(val);
    }

    @Override
    public void writeByte(int val) throws IOException {
        stream.writeByte(val);
    }

    @Override
    public void writeShort(int val) throws IOException {
        stream.writeShort(Integer.reverseBytes(val) >> 16);
    }

    @Override
    public void writeChar(int val) throws IOException {
        stream.writeChar(Integer.reverseBytes(val) >>> 16);
    }

    @Override
    public void writeInt(int val) throws IOException {
        stream.writeInt(Integer.reverseBytes(val));
    }

    @Override
    public void writeLong(long val) throws IOException {
        stream.writeLong(Long.reverseBytes(val));
    }

    @Override
    public void writeFloat(float val) throws IOException {
        writeInt(Float.floatToIntBits(val));
    }

    @Override
    public void writeDouble(double val) throws IOException {
        writeLong(Double.doubleToLongBits(val));
    }

    @Override
    public void writeBytes(String val) throws IOException {
        stream.writeBytes(val);
    }

    @Override
    public void writeChars(String s) throws IOException {
        stream.writeChars(s);
    }

    @Override
    public void writeUTF(String s) throws IOException {
        stream.writeUTF(s);
    }
}
