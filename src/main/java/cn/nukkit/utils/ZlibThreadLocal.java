package cn.nukkit.utils;

import cn.nukkit.nbt.stream.FastByteArrayOutputStream;

import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public final class ZlibThreadLocal implements ZlibProvider {
    private static final ThreadLocal<Inflater> INFLATER = ThreadLocal.withInitial(Inflater::new);
    private static final ThreadLocal<Deflater> DEFLATER = ThreadLocal.withInitial(Deflater::new);
    private static final ThreadLocal<byte[]> BUFFER = ThreadLocal.withInitial(() -> new byte[8192]);

    @Override
    public byte[] deflate(byte[][] datas, int level) throws IOException {
        Deflater deflater = DEFLATER.get();
        FastByteArrayOutputStream bos = ThreadCache.fbaos.get();
        try {
            deflater.reset();
            deflater.setLevel(level);
            bos.reset();
            byte[] buffer = BUFFER.get();

            for (byte[] data : datas) {
                deflater.setInput(data);
                while (!deflater.needsInput()) {
                    int i = deflater.deflate(buffer);
                    bos.write(buffer, 0, i);
                }
            }
            deflater.finish();
            while (!deflater.finished()) {
                int i = deflater.deflate(buffer);
                bos.write(buffer, 0, i);
            }
        } finally {
            // https://bugs.java.com/bugdatabase/view_bug.do?bug_id=6293787
            deflater.reset();
        }
        //Deflater::end is called the time when the process exits.
        return bos.toByteArray();
    }

    @Override
    public byte[] deflate(byte[] data, int level) throws IOException {
        Deflater deflater = DEFLATER.get();
        FastByteArrayOutputStream bos = ThreadCache.fbaos.get();
        try {
            deflater.reset();
            deflater.setLevel(level);
            deflater.setInput(data);
            deflater.finish();
            bos.reset();
            byte[] buffer = BUFFER.get();
            while (!deflater.finished()) {
                int i = deflater.deflate(buffer);
                bos.write(buffer, 0, i);
            }
        } finally {
            deflater.reset();
        }
        //Deflater::end is called the time when the process exits.
        return bos.toByteArray();
    }

    @Override
    public byte[] inflate(byte[] data, int maxSize) throws IOException {
        Inflater inflater = INFLATER.get();
        try {
            inflater.reset();
            inflater.setInput(data);
            inflater.finished();
            FastByteArrayOutputStream bos = ThreadCache.fbaos.get();
            bos.reset();

            byte[] buffer = BUFFER.get();
            try {
                int length = 0;
                while (!inflater.finished()) {
                    int i = inflater.inflate(buffer);
                    length += i;
                    if (maxSize > 0 && length >= maxSize) {
                        throw new IOException("Inflated data exceeds maximum size");
                    }
                    bos.write(buffer, 0, i);
                }
                return bos.toByteArray();
            } catch (DataFormatException e) {
                throw new IOException("Unable to inflate zlib stream", e);
            }
        } finally {
            inflater.reset();
        }
    }
}
