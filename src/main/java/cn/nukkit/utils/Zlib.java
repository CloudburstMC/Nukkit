package cn.nukkit.utils;

import cn.nukkit.api.PowerNukkitDifference;

import java.io.IOException;
import java.util.zip.Deflater;


public abstract class Zlib {
    private static ZlibProvider[] providers;
    private static ZlibProvider provider;

    static {
        providers = new ZlibProvider[3];
        providers[2] = new ZlibThreadLocal();
        provider = providers[2];
    }

    public static void setProvider(int providerIndex) {
        MainLogger.getLogger().info("Selected Zlib Provider: " + providerIndex + " (" + provider.getClass().getCanonicalName() + ")");
        switch (providerIndex) {
            case 0:
                if (providers[providerIndex] == null)
                    providers[providerIndex] = new ZlibOriginal();
                break;
            case 1:
                if (providers[providerIndex] == null)
                    providers[providerIndex] = new ZlibSingleThreadLowMem();
                break;
            case 2:
                if (providers[providerIndex] == null)
                    providers[providerIndex] = new ZlibThreadLocal();
                break;
            default:
                throw new UnsupportedOperationException("Invalid provider: " + providerIndex);
        }
        if (providerIndex != 2) {
            MainLogger.getLogger().warning(" - This Zlib will negatively affect performance");
        }
        provider = providers[providerIndex];
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Throws IOException instead of Exception")
    public static byte[] deflate(byte[] data) throws IOException {
        return deflate(data, Deflater.DEFAULT_COMPRESSION);
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Throws IOException instead of Exception")
    public static byte[] deflate(byte[] data, int level) throws IOException {
        return provider.deflate(data, level);
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Throws IOException instead of Exception")
    public static byte[] deflate(byte[][] data, int level) throws IOException {
        return provider.deflate(data, level);
    }

    public static byte[] inflate(byte[] data) throws IOException {
        return inflate(data, -1);
    }

    public static byte[] inflate(byte[] data, int maxSize) throws IOException {
        return provider.inflate(data, maxSize);
    }
}
