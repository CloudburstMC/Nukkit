package cn.nukkit.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public class ZlibOriginal implements ZlibProvider {


    @Override
    public byte[] deflate(byte[][] datas, int level) throws Exception {
        int len = 0;
        for (byte[] arr : datas) len += arr.length;
        Deflater deflater = new Deflater(level);
        deflater.reset();
        ByteArrayOutputStream bos = new ByteArrayOutputStream(len);
        DeflaterOutputStream out = new DeflaterOutputStream(bos, deflater);
        for (byte[] data : datas) {
            out.write(data);
        }
        out.close();
        return bos.toByteArray();
    }

    @Override
    public byte[] deflate(byte[] data, int level) throws Exception {
        Deflater deflater = new Deflater(level);
        deflater.reset();
        deflater.setInput(data);
        deflater.finish();
        ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);
        byte[] buf = new byte[1024];
        try {
            while (!deflater.finished()) {
                int i = deflater.deflate(buf);
                bos.write(buf, 0, i);
            }
        } finally {
            deflater.end();
        }
        return bos.toByteArray();
    }

    @Override
    public byte[] inflate(InputStream stream) throws IOException {
        InflaterInputStream inputStream = new InflaterInputStream(stream);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;

        try {
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
        } finally {
            buffer = outputStream.toByteArray();
            outputStream.flush();
            outputStream.close();
            inputStream.close();
        }

        return buffer;
    }
}
