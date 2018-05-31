package com.nukkitx.server.network.util;

import com.nukkitx.server.util.NativeCodeFactory;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import net.md_5.bungee.jni.zlib.BungeeZlib;

import java.util.zip.DataFormatException;
import java.util.zip.Deflater;


public class Zlib {
    public static final Zlib DEFAULT = new Zlib(Deflater.DEFAULT_COMPRESSION);

    private final ThreadLocal<BungeeZlib> inflaterLocal;
    private final ThreadLocal<BungeeZlib> deflaterLocal;

    public Zlib(int compression) {
        inflaterLocal = ThreadLocal.withInitial(() -> {
            BungeeZlib zlib = NativeCodeFactory.zlib.newInstance();
            zlib.init(false, compression);
            return zlib;
        });

        deflaterLocal = ThreadLocal.withInitial(() -> {
            BungeeZlib zlib = NativeCodeFactory.zlib.newInstance();
            zlib.init(true, compression);
            return zlib;
        });
    }

    public ByteBuf inflate(ByteBuf buffer) throws DataFormatException {
        // Ensure that this buffer is direct.
        if (buffer.getByte(0) != 0x78) throw new DataFormatException("No zlib header");
        ByteBuf source = null;
        ByteBuf decompressed = PooledByteBufAllocator.DEFAULT.directBuffer();

        try {
            if (!buffer.isDirect()) {
                // We don't have a direct buffer. Create one.
                ByteBuf temporary = PooledByteBufAllocator.DEFAULT.directBuffer();
                temporary.writeBytes(buffer);
                source = temporary;
            } else {
                source = buffer;
            }

            inflaterLocal.get().process(source, decompressed);
            return decompressed;
        } catch (DataFormatException e) {
            decompressed.release();
            throw e;
        } finally {
            if (source != null && source != buffer) {
                source.release();
            }
        }
    }

    public ByteBuf deflate(ByteBuf buffer) throws DataFormatException {
        ByteBuf dest = PooledByteBufAllocator.DEFAULT.directBuffer();
        try {
            deflate(buffer, dest);
        } catch (DataFormatException e) {
            dest.release();
            throw e;
        }
        return dest;
    }

    public void deflate(ByteBuf toCompress, ByteBuf into) throws DataFormatException {
        ByteBuf destination = null;
        ByteBuf source = null;
        try {
            if (!toCompress.isDirect()) {
                // Source is not a direct buffer. Work on a temporary direct buffer and then write the contents out.
                source = PooledByteBufAllocator.DEFAULT.directBuffer();
                source.writeBytes(toCompress);
            } else {
                source = toCompress;
            }

            if (!into.isDirect()) {
                // Destination is not a direct buffer. Work on a temporary direct buffer and then write the contents out.
                destination = PooledByteBufAllocator.DEFAULT.directBuffer();
            } else {
                destination = into;
            }

            deflaterLocal.get().process(source, destination);

            if (destination != into) {
                into.writeBytes(destination);
            }
        } finally {
            if (source != null && source != toCompress) {
                source.release();
            }
            if (destination != null && destination != into) {
                destination.release();
            }
        }
    }
}
