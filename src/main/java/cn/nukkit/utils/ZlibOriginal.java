package cn.nukkit.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.Deflater;
import java.util.zip.InflaterInputStream;

public class ZlibOriginal implements ZlibProvider {

    @Override
    public byte[] deflate(byte[][] datas, int level) throws Exception {
        Deflater deflater = new Deflater(level);
        deflater.reset();
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream bos = new ByteArrayOutputStream(datas.length);
        try {
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
        } finally {
            deflater.end();
        }
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
