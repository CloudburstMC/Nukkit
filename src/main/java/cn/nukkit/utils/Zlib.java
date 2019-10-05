package cn.nukkit.utils;

import com.nukkitx.natives.util.Natives;
import com.nukkitx.natives.zlib.Deflater;
import com.nukkitx.natives.zlib.Inflater;
import com.nukkitx.network.util.Preconditions;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;

import java.nio.ByteBuffer;
import java.util.zip.DataFormatException;


public final class Zlib {
    public static final Zlib DEFAULT = new Zlib(-1, false);
    public static final Zlib GZIP = new Zlib(-1, true);
    private static final com.nukkitx.natives.zlib.Zlib ZLIB = Natives.ZLIB.get();
    private final ThreadLocal<Inflater> inflaterThreadLocal;
    private final ThreadLocal<Deflater> deflaterThreadLocal;

    public Zlib(int level, boolean nowrap) {
        this.inflaterThreadLocal = ThreadLocal.withInitial(() -> ZLIB.create(nowrap));
        this.deflaterThreadLocal = ThreadLocal.withInitial(() -> ZLIB.create(level, nowrap));
    }

    public void inflate(ByteBuf input, ByteBuf output, int maxSize) throws DataFormatException {
        Preconditions.checkArgument(input.readableBytes() < maxSize,
                "input is larger than maxSize: %s", maxSize);
        Inflater inflater = inflaterThreadLocal.get();
        inflater.reset();

        inflater.setInput(input.internalNioBuffer(input.readerIndex(), input.readableBytes()));

        int startIndex = output.writerIndex();
        while (!inflater.finished()) {
            Preconditions.checkArgument(output.writerIndex() - startIndex < maxSize,
                    "inflated buffer is larger than maxSize: %s", maxSize);
            output.ensureWritable(8192);
            ByteBuffer internalBuffer = output.internalNioBuffer(output.writerIndex(), output.writableBytes());
            int result = inflater.inflate(internalBuffer);
            output.writerIndex(output.writerIndex() + result);
        }
    }

    public void inflate(ByteBuf input, ByteBuf output) throws DataFormatException {
        Inflater inflater = inflaterThreadLocal.get();
        inflater.reset();

        inflater.setInput(input.internalNioBuffer(input.readerIndex(), input.readableBytes()));

        while (!inflater.finished()) {
            output.ensureWritable(8192);
            ByteBuffer internalBuffer = output.internalNioBuffer(output.writerIndex(), output.writableBytes());
            int result = inflater.inflate(internalBuffer);
            output.writerIndex(output.writerIndex() + result);
        }
    }

    public byte[] inflate(byte[] input) throws DataFormatException {
        ByteBuf inputBuffer = Unpooled.wrappedBuffer(input);
        ByteBuf outputBuffer = ByteBufAllocator.DEFAULT.directBuffer();
        try {
            this.inflate(inputBuffer, outputBuffer);

            byte[] output = new byte[outputBuffer.readableBytes()];
            outputBuffer.readBytes(output);
            return output;
        } finally {
            outputBuffer.release();
        }
    }

    public void deflate(ByteBuf input, ByteBuf output, int level) {
        Deflater deflater = this.deflaterThreadLocal.get();
        deflater.reset();
        deflater.setLevel(level);

        deflater.setInput(input.internalNioBuffer(input.readerIndex(), input.readableBytes()));

        while (!deflater.finished()) {
            output.ensureWritable(8192);
            ByteBuffer internalBuffer = output.internalNioBuffer(output.writerIndex(), output.writableBytes());
            int result = deflater.deflate(internalBuffer);
            output.writerIndex(output.writerIndex() + result);
        }
    }

    public byte[] deflate(byte[] input, int level) {
        ByteBuf inputBuffer = Unpooled.wrappedBuffer(input);
        ByteBuf outputBuffer = ByteBufAllocator.DEFAULT.directBuffer();
        try {
            this.deflate(inputBuffer, outputBuffer, level);

            byte[] output = new byte[outputBuffer.readableBytes()];
            outputBuffer.readBytes(output);
            return output;
        } finally {
            outputBuffer.release();
        }
    }

}
