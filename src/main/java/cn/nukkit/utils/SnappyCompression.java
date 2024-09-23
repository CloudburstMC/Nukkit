package cn.nukkit.utils;

import org.xerial.snappy.Snappy;

import java.io.IOException;

/**
 * Implements data compression and decompression using Snappy, powered by snappy-java (org.xerial.snappy)
 * @author PetteriM1
 */
public class SnappyCompression {

    public static byte[] compress(byte[] data) throws IOException {
        return Snappy.compress(data);
    }

    public static byte[] decompress(byte[] data, int maxSize) throws IOException {
        int size = Snappy.uncompressedLength(data);
        if (size > maxSize && maxSize > 0) {
            throw new IllegalArgumentException("Input is too big");
        }
        byte[] decompressed = new byte[size];
        Snappy.uncompress(data, 0, data.length, decompressed, 0);
        return decompressed;
    }
}
