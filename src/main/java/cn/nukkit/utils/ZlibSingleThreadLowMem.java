package cn.nukkit.utils;

import cn.nukkit.nbt.stream.FastByteArrayOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public class ZlibSingleThreadLowMem implements ZlibProvider {
    static final int BUFFER_SIZE = 8192;
    Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
    byte[] buffer = new byte[BUFFER_SIZE];

    @Override
    public byte[] deflate(byte[][] datas, int level) throws Exception {
        deflater.reset();
        FastByteArrayOutputStream bos = new FastByteArrayOutputStream(buffer);
        DeflaterOutputStream out = new DeflaterOutputStream(bos, deflater);
        for (byte[] data : datas) {
            out.write(data);
        }
        out.close();
        return bos.toByteArray();
    }

    @Override
    synchronized public byte[] deflate(byte[] data, int level) throws Exception {
        deflater.reset();
        deflater.setInput(data);
        deflater.finish();
        ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);
        try {
            while (!deflater.finished()) {
                int i = deflater.deflate(buffer);
                bos.write(buffer, 0, i);
            }
        } finally {
            //deflater.end();
        }
        return bos.toByteArray();
    }

    @Override
    synchronized public byte[] inflate(InputStream stream) throws IOException {
        InflaterInputStream inputStream = new InflaterInputStream(stream);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(BUFFER_SIZE);
        byte[] bufferOut;
        int length;

        try {
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
        } finally {
            outputStream.flush();
            bufferOut = outputStream.toByteArray();
            outputStream.close();
            inputStream.close();
        }

        return bufferOut;
    }
}
