package cn.nukkit.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.*;


public abstract class ZLibUtils {

    public static byte[] compress(byte[] data) throws Exception {
        byte[] output;

        Deflater compresser = new Deflater();

        compresser.reset();
        compresser.setInput(data);
        compresser.finish();
        ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);
        byte[] buf = new byte[1024];
        while (!compresser.finished()) {
            int i = compresser.deflate(buf);
            bos.write(buf, 0, i);
        }
        output = bos.toByteArray();

        compresser.end();
        return output;
    }

    public static void compress(byte[] data, OutputStream os) throws IOException {
        DeflaterOutputStream dos = new DeflaterOutputStream(os);
        dos.write(data, 0, data.length);
        dos.finish();
        dos.flush();
    }

    public static byte[] decompress(byte[] data) throws DataFormatException, IOException {
        byte[] output = new byte[0];

        Inflater decompresser = new Inflater();
        decompresser.reset();
        decompresser.setInput(data);

        try (ByteArrayOutputStream o = new ByteArrayOutputStream(data.length)) {
            byte[] buf = new byte[1024];
            while (!decompresser.finished()) {
                int i = decompresser.inflate(buf);
                o.write(buf, 0, i);
            }
            output = o.toByteArray();
        } catch (Exception e) {
            output = data;
            throw e;
        }

        decompresser.end();
        return output;
    }

    public static byte[] decompress(InputStream is) throws IOException {
        InflaterInputStream iis = new InflaterInputStream(is);
        ByteArrayOutputStream o = new ByteArrayOutputStream(1024);

        int i = 1024;
        byte[] buf = new byte[i];

        while ((i = iis.read(buf, 0, i)) > 0) {
            o.write(buf, 0, i);
        }

        return o.toByteArray();
    }

}

