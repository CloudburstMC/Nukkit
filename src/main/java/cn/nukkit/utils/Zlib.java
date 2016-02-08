package cn.nukkit.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;


public abstract class Zlib {

    public static byte[] deflate(byte[] data) throws Exception {
        return deflate(data, Deflater.DEFAULT_COMPRESSION);
    }

    public static byte[] deflate(byte[] data, int level) throws Exception {
        Deflater deflater = new Deflater(level);
        deflater.reset();
        deflater.setInput(data);
        deflater.finish();
        ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);
        byte[] buf = new byte[1024];
        while (!deflater.finished()) {
            int i = deflater.deflate(buf);
            bos.write(buf, 0, i);
        }
        deflater.end();
        return bos.toByteArray();
    }

    public static byte[] inflate(byte[] data) throws DataFormatException, IOException {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream o = new ByteArrayOutputStream(data.length);
        byte[] buf = new byte[1024];
        while (!inflater.finished()) {
            int i = inflater.inflate(buf);
            o.write(buf, 0, i);
        }
        inflater.end();
        return o.toByteArray();
    }

    public static byte[] inflate(byte[] data, int maxSize) throws DataFormatException, IOException {
        return Binary.subBytes(inflate(data), 0, maxSize);
    }

}

