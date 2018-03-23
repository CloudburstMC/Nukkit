package cn.nukkit.utils;

import cn.nukkit.nbt.stream.FastByteArrayOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.Deflater;
import java.util.zip.InflaterInputStream;

public final class ZlibThreadLocal implements ZlibProvider {
    @Override
    public byte[] deflate(byte[][] datas, int level) throws Exception {
        Deflater deflater = getDef(level);
        if (deflater == null) throw new IllegalArgumentException("No deflate for level " + level + " !");
        deflater.reset();
        FastByteArrayOutputStream bos = ThreadCache.fbaos.get();
        bos.reset();
        byte[] buffer = buf.get();

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
    public byte[] deflate(byte[] data, int level) throws Exception {
        Deflater deflater = getDef(level);
        if (deflater == null) throw new IllegalArgumentException("No deflate for level " + level + " !");
        deflater.reset();
        deflater.setInput(data);
        deflater.finish();
        FastByteArrayOutputStream bos = ThreadCache.fbaos.get();
        bos.reset();
        byte[] buffer = buf.get();
        while (!deflater.finished()) {
            int i = deflater.deflate(buffer);
            bos.write(buffer, 0, i);
        }
        //Deflater::end is called the time when the process exits.
        return bos.toByteArray();
    }

    /* -=-=-=-=-=- Internal -=-=-=-=-=- Do NOT attempt to use in production -=-=-=-=-=- */


    public static final IterableThreadLocal<byte[]> buf = new IterableThreadLocal<byte[]>() {
        @Override
        public byte[] init() {
            return new byte[8192];
        }
    };

    public static final IterableThreadLocal<Deflater> def = new IterableThreadLocal<Deflater>() {
        @Override
        public Deflater init() {
            return new Deflater();
        }
    };

    private static Deflater getDef(int level) {
        def.get().setLevel(level);
        return def.get();
    }

    @Override
    public byte[] inflate(InputStream stream) throws IOException {
        InflaterInputStream inputStream = new InflaterInputStream(stream);
        FastByteArrayOutputStream outputStream = ThreadCache.fbaos.get();
        outputStream.reset();
        int length;

        byte[] result;
        byte[] buffer = buf.get();
        try {
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
        } finally {
            result = outputStream.toByteArray();
            outputStream.flush();
            outputStream.close();
            inputStream.close();
        }

        return result;
    }
}
