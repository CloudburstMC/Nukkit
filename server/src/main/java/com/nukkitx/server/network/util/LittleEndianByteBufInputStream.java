package com.nukkitx.server.network.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;

import java.io.IOException;

public class LittleEndianByteBufInputStream extends ByteBufInputStream {

    public LittleEndianByteBufInputStream(ByteBuf buffer) {
        super(buffer);
    }

    @Override
    public short readShort() throws IOException {
        return Short.reverseBytes(super.readShort());
    }

    @Override
    public long readLong() throws IOException {
        return Long.reverseBytes(super.readLong());
    }

    @Override
    public int readInt() throws IOException {
        return Integer.reverseBytes(super.readInt());
    }
}
