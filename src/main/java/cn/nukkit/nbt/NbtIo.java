package cn.nukkit.nbt;

import cn.nukkit.item.Item;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class NbtIo {
    public static CompoundTag putItemHelper(Item item) {
        return putItemHelper(item, null);
    }

    public static CompoundTag putItemHelper(Item item, Integer slot) {
        CompoundTag tag = new CompoundTag(null)
                .putShort("id", (short) item.getId())
                .putByte("Count", (byte) item.getCount())
                .putShort("Damage", item.getDamage());
        if (slot != null) {
            tag.putByte("Slot", (byte) (int) slot);
        }

        if (item.hasCompoundTag()) {
            tag.putCompound("tag", item.getNamedTag());
        }

        return tag;
    }

    public static Item getItemHelper(CompoundTag tag) {
        if (!tag.contains("id") || !tag.contains("Count")) {
            return Item.get(0);
        }

        Item item = Item.get(tag.getShort("id"), (int) (!tag.contains("Damage") ? 0 : tag.getShort("Damage")), tag.getByte("Count"));

        if (tag.contains("tag") && tag.get("tag") instanceof CompoundTag) {
            item.setNamedTag(tag.getCompound("tag"));
        }

        return item;
    }

    public static CompoundTag readCompressed(InputStream in) throws IOException {
        try (DataInputStream dis = new DataInputStream(new BufferedInputStream(new GZIPInputStream(in)))) {
            return read(dis);
        }
    }

    public static byte[] writeCompressed(CompoundTag tag) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream stream = new DataOutputStream(baos);
        writeCompressed(tag, stream);
        return baos.toByteArray();
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

    public static byte[] write(CompoundTag tag) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream stream = new DataOutputStream(baos);
        Tag.writeNamedTag(tag, stream);
        return baos.toByteArray();
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

    public static CompoundTag read(byte[] data) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream stream = new DataInputStream(bais);
        return read(stream);
    }

    public static void write(CompoundTag tag, DataOutput dos) throws IOException {
        Tag.writeNamedTag(tag, dos);
    }
}
