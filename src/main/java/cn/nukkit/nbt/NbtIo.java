package cn.nukkit.nbt;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class NbtIo {
    public static CompoundTag readCompressed(InputStream in) throws IOException {
        try (DataInputStream dis = new DataInputStream(new BufferedInputStream(new GZIPInputStream(in)))) {
            return read(dis);
        }
    }

    public static void writeCompressed(CompoundTag tag, OutputStream out) throws IOException {
        try (DataOutputStream dos = new DataOutputStream(new GZIPOutputStream(out))) {
            write(tag, dos);
        }
    }

    public static CompoundTag decompress(byte[] buffer) throws IOException {
        try (DataInputStream dis = new DataInputStream(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(buffer))))) {
            return read(dis);
        }
    }

    public static byte[] compress(CompoundTag tag) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (DataOutputStream dos = new DataOutputStream(new GZIPOutputStream(baos))) {
            write(tag, dos);
        }
        return baos.toByteArray();
    }

    public static void safeWrite(CompoundTag tag, File file) throws IOException {
        File file2 = new File(file.getAbsolutePath() + "_tmp");
        if (file2.exists()) file2.delete();
        write(tag, file2);
        if (file.exists()) file.delete();
        if (file.exists()) throw new IOException("Failed to delete " + file);
        file2.renameTo(file);
    }

    public static void write(CompoundTag tag, File file) throws IOException {
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(file))) {
            write(tag, dos);
        }
    }

    public static CompoundTag read(File file) throws IOException {
        if (!file.exists()) return null;
        try (DataInputStream dis = new DataInputStream(new FileInputStream(file))) {
            return read(dis);
        }
    }

    public static CompoundTag read(DataInput dis) throws IOException {
        Tag tag = Tag.readNamedTag(dis);
        if (tag instanceof CompoundTag) return (CompoundTag) tag;
        throw new IOException("Root tag must be a named compound tag");
    }

    public static void write(CompoundTag tag, DataOutput dos) throws IOException {
        Tag.writeNamedTag(tag, dos);
    }
}
