package com.nukkitx.nbt.stream;

import javax.annotation.Nonnull;
import java.io.*;

public class LittleEndianDataOutputStream implements DataOutput, Closeable {
    private final DataOutputStream stream;

    public LittleEndianDataOutputStream(OutputStream stream) {
        this.stream = new DataOutputStream(stream);
    }

    public LittleEndianDataOutputStream(DataOutputStream stream) {
        this.stream = stream;
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }

    @Override
    public void write(int bytes) throws IOException {
        stream.write(bytes);
    }

    @Override
    public void write(@Nonnull byte[] bytes) throws IOException {
        stream.write(bytes);
    }

    @Override
    public void write(@Nonnull byte[] bytes, int offset, int length) throws IOException {
        stream.write(bytes, offset, length);
    }

    @Override
    public void writeBoolean(boolean value) throws IOException {
        stream.writeBoolean(value);
    }

    @Override
    public void writeByte(int value) throws IOException {
        stream.writeByte(value);
    }

    @Override
    public void writeShort(int value) throws IOException {
        stream.writeShort(Integer.reverseBytes(value) >> 16);
    }

    @Override
    public void writeChar(int value) throws IOException {
        stream.writeChar(Character.reverseBytes((char) value));
    }

    @Override
    public void writeInt(int value) throws IOException {
        stream.writeInt(Integer.reverseBytes(value));
    }

    @Override
    public void writeLong(long value) throws IOException {
        stream.writeLong(Long.reverseBytes(value));
    }

    @Override
    public void writeFloat(float value) throws IOException {
        writeInt(Float.floatToIntBits(value));
    }

    @Override
    public void writeDouble(double value) throws IOException {
        writeLong(Double.doubleToLongBits(value));
    }

    @Override
    public void writeBytes(@Nonnull String string) throws IOException {
        stream.writeBytes(string);
    }

    @Override
    public void writeChars(@Nonnull String string) throws IOException {
        stream.writeChars(string);
    }

    @Override
    public void writeUTF(@Nonnull String string) throws IOException {
        stream.writeUTF(string);
    }
}
