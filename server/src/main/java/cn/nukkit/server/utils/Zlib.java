package cn.nukkit.server.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.Deflater;
import java.util.zip.InflaterInputStream;


public abstract class Zlib {
    
    public static byte[] deflate(byte[] data) throws Exception {
        return deflate(data, Deflater.DEFAULT_COMPRESSION);
    }

    public static byte[] deflate(byte[] data, int level) throws Exception {
        Deflater deflater = getDef(level);
        if (deflater == null) throw new IllegalArgumentException("No deflate for level " + level + " !");
        deflater.reset();
        deflater.setInput(data);
        deflater.finish();
        ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);
        while (!deflater.finished()) {
            int i = deflater.deflate(buf.get());
            bos.write(buf.get(), 0, i);
        }
        //Deflater::end is called the time when the process exits.
        return bos.toByteArray();
    }

    public static byte[] inflate(byte[] data) throws IOException {
        return inflate(new ByteArrayInputStream(data));
    }

    public static byte[] inflate(byte[] data, int maxSize) throws IOException {
        return inflate(new ByteArrayInputStream(data, 0, maxSize));
    }

    /* -=-=-=-=-=- Internal -=-=-=-=-=- Do NOT attempt to use in production -=-=-=-=-=- */

    private static final ThreadLocal<byte[]> buf = ThreadLocal.withInitial(() -> new byte[1024]);
    private static final ThreadLocal<Deflater> def = ThreadLocal.withInitial(Deflater::new);

    private static Deflater getDef(int level) {
        def.get().setLevel(level);
        return def.get();
    }

    private static byte[] inflate(InputStream stream) throws IOException {
        InflaterInputStream inputStream = new InflaterInputStream(stream);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int length;

        try {
            while ((length = inputStream.read(buf.get())) != -1) {
                outputStream.write(buf.get(), 0, length);
            }
        } finally {
            buf.set(outputStream.toByteArray());
            outputStream.flush();
            outputStream.close();
            inputStream.close();
        }

        return buf.get();
    }
}