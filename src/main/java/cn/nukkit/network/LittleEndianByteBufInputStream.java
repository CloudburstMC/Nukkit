package cn.nukkit.network;

import cn.nukkit.api.Since;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;

import javax.annotation.Nonnull;
import java.io.IOException;

@Since("1.4.0.0-PN")
public class LittleEndianByteBufInputStream extends ByteBufInputStream {

    private final ByteBuf buffer;

    @Since("1.4.0.0-PN")
    public LittleEndianByteBufInputStream(@Nonnull ByteBuf buffer) {
        super(buffer);
        this.buffer = buffer;
    }

    @Override
    public char readChar() throws IOException {
        return Character.reverseBytes(buffer.readChar());
    }

    @Override
    public double readDouble() throws IOException {
        return buffer.readDoubleLE();
    }

    @Override
    public float readFloat() throws IOException {
        return buffer.readFloatLE();
    }

    @Override
    public short readShort() throws IOException {
        return buffer.readShortLE();
    }

    @Override
    public int readUnsignedShort() throws IOException {
        return buffer.readUnsignedShortLE();
    }

    @Override
    public long readLong() throws IOException {
        return buffer.readLongLE();
    }

    @Override
    public int readInt() throws IOException {
        return buffer.readIntLE();
    }
}
