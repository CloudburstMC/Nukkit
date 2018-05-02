package com.nukkitx.server.network.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;

public class LittleEndianByteBufOutputStream extends ByteBufOutputStream {

    public LittleEndianByteBufOutputStream(ByteBuf buffer) {
        super(buffer);
    }

    @Override
    public void writeShort(int val) {
        buffer().writeShortLE(val);
    }

    @Override
    public void writeLong(long val) {
        buffer().writeLongLE(val);
    }

    @Override
    public void writeInt(int val) {
        buffer().writeIntLE(val);
    }
}
