package cn.nukkit.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.zip.Deflater;


public abstract class Zlib {
    static List<ZlibProvider> providers;
    static ZlibProvider provider;

    static {
      providers = Arrays.asList(new ZlibOriginal(), new ZlibSingleThreadLowMem(), new ZlibThreadLocal());
      provider = providers.get(0);
    }

    public static void setProvider(int providerIndex) {
      provider = providers.get(providerIndex);
      System.out.println("Selected Zlib Provider: " + provider.getClass().getCanonicalName());
    }

    public static byte[] deflate(byte[] data) throws Exception {
        return deflate(data, Deflater.DEFAULT_COMPRESSION);
    }

    public static byte[] deflate(byte[] data, int level) throws Exception {
        return provider.deflate(data, level);
    }

    public static byte[] inflate(InputStream stream) throws IOException {
        return provider.inflate(stream);
    }

    public static byte[] inflate(byte[] data) throws IOException {
        return inflate(new ByteArrayInputStream(data));
    }

    public static byte[] inflate(byte[] data, int maxSize) throws IOException {
        return inflate(new ByteArrayInputStream(data, 0, maxSize));
    }
}
