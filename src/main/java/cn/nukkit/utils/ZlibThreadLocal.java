package cn.nukkit.utils;

import cn.nukkit.Server;
import cn.nukkit.nbt.stream.FastByteArrayOutputStream;

import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public final class ZlibThreadLocal implements ZlibProvider {

    private static final ThreadLocal<Inflater> INFLATER = ThreadLocal.withInitial(Inflater::new);
    private static final ThreadLocal<Deflater> DEFLATER = ThreadLocal.withInitial(Deflater::new);
    private static final ThreadLocal<Inflater> INFLATER_RAW = ThreadLocal.withInitial(() -> new Inflater(true));
    private static final ThreadLocal<Deflater> DEFLATER_RAW = ThreadLocal.withInitial(() -> new Deflater(7, true));
    private static final ThreadLocal<byte[]> BUFFER = ThreadLocal.withInitial(() -> new byte[32768]);

    @Override
    public byte[] deflate(byte[][] datas, int level) throws IOException {
        Deflater deflater = DEFLATER.get();
        deflater.reset();
        deflater.setLevel(level);
        FastByteArrayOutputStream bos = ThreadCache.fbaos.get();
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
        //Deflater::end is called the time when the process exits.
        return bos.toByteArray();
    }

    @Override
    public byte[] deflate(byte[] data, int level) throws IOException {
        Deflater deflater = DEFLATER.get();
        deflater.reset();
        deflater.setLevel(level);
        deflater.setInput(data);
        deflater.finish();
        FastByteArrayOutputStream bos = ThreadCache.fbaos.get();
        bos.reset();
        byte[] buffer = BUFFER.get();
        while (!deflater.finished()) {
            int i = deflater.deflate(buffer);
            bos.write(buffer, 0, i);
        }
        //Deflater::end is called the time when the process exits.
        return bos.toByteArray();
    }

    @Override
    public byte[] deflateRaw(byte[][] datas, int level) throws IOException {
        Deflater deflater = DEFLATER_RAW.get();
        deflater.reset();
        deflater.setLevel(level);
        FastByteArrayOutputStream bos = ThreadCache.fbaos.get();
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
        //Deflater::end is called the time when the process exits.
        return bos.toByteArray();
    }

    @Override
    public byte[] deflateRaw(byte[] data, int level) throws IOException {
        Deflater deflater = DEFLATER_RAW.get();
        deflater.reset();
        deflater.setLevel(data.length < Server.getInstance().networkCompressionThreshold ? 0 : level);
        deflater.setInput(data);
        deflater.finish();
        FastByteArrayOutputStream bos = ThreadCache.fbaos.get();
        bos.reset();
        byte[] buffer = BUFFER.get();
        while (!deflater.finished()) {
            int i = deflater.deflate(buffer);
            bos.write(buffer, 0, i);
        }
        //Deflater::end is called the time when the process exits.
        return bos.toByteArray();
    }

    @Override
    public byte[] inflate(byte[] data, int maxSize) throws IOException {
        Inflater inflater = INFLATER.get();
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
                if (i == 0) {
                    throw new IOException("Could not decompress data");
                }
                length += i;
                if (maxSize > 0 && length >= maxSize) {
                    throw new IOException("Inflated data exceeds maximum size");
                }
                bos.write(buffer, 0, i);
            }
            return bos.toByteArray();
        } catch (DataFormatException e) {
            throw new IOException("Unable to inflate Zlib stream", e);
        }
    }

    @Override
    public byte[] inflateRaw(byte[] data, int maxSize) throws IOException {
        Inflater inflater = INFLATER_RAW.get();
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
                if (i == 0) {
                    throw new IOException("Could not decompress data");
                }
                length += i;
                if (maxSize > 0 && length >= maxSize) {
                    throw new IOException("Inflated data exceeds maximum size");
                }
                bos.write(buffer, 0, i);
            }
            return bos.toByteArray();
        } catch (DataFormatException e) {
            throw new IOException("Unable to inflate Zlib stream", e);
        }
    }
}
