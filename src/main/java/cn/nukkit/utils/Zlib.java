package cn.nukkit.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;


public abstract class Zlib {

    public static byte[] deflate(byte[] data) throws Exception {
        return deflate(data, -1);
    }

    public static byte[] deflate(byte[] data, int level) throws Exception {
        byte[] output;
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
        output = bos.toByteArray();
        deflater.end();
        return output;
    }

    public static byte[] inflate(byte[] data) throws DataFormatException, IOException {
        byte[] output;
        Inflater inflater = new Inflater();
        inflater.reset();
        inflater.setInput(data);
        ByteArrayOutputStream o = new ByteArrayOutputStream(data.length);
        byte[] buf = new byte[1024];
        while (!inflater.finished()) {
            int i = inflater.inflate(buf);
            o.write(buf, 0, i);
        }
        output = o.toByteArray();
        inflater.end();
        return output;
    }

}

