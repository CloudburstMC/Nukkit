package cn.nukkit.utils;

import java.io.IOException;
import java.util.zip.Deflater;

public abstract class Zlib {

    private static final ZlibProvider provider = new ZlibThreadLocal();

    public static byte[] deflate(byte[] data) throws Exception {
        return deflate(data, Deflater.DEFAULT_COMPRESSION);
    }

    public static byte[] deflate(byte[] data, int level) throws Exception {
        return provider.deflate(data, level);
    }

    public static byte[] deflate(byte[][] data, int level) throws Exception {
        return provider.deflate(data, level);
    }

    public static byte[] deflateRaw(byte[] data, int level) throws Exception {
        return provider.deflateRaw(data, level);
    }

    public static byte[] deflateRaw(byte[][] data, int level) throws Exception {
        return provider.deflateRaw(data, level);
    }

    public static byte[] inflate(byte[] data) throws IOException {
        return inflate(data, -1);
    }

    public static byte[] inflate(byte[] data, int maxSize) throws IOException {
        return provider.inflate(data, maxSize);
    }

    public static byte[] inflateRaw(byte[] data, int maxSize) throws IOException {
        return provider.inflateRaw(data, maxSize);
    }
}
