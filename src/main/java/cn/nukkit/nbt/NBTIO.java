package cn.nukkit.nbt;

import cn.nukkit.item.Item;
import cn.nukkit.nbt.stream.FastByteArrayOutputStream;
import cn.nukkit.nbt.stream.NBTInputStream;
import cn.nukkit.nbt.stream.NBTOutputStream;
import cn.nukkit.nbt.stream.PGZIPOutputStream;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.ThreadCache;
import java.io.*;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;

/**
 * A Named Binary Tag library for Nukkit Project
 */
public class NBTIO {

    public static CompoundTag putItemHelper(final Item item) {
        return NBTIO.putItemHelper(item, null);
    }

    public static CompoundTag putItemHelper(final Item item, final Integer slot) {
        final CompoundTag tag = new CompoundTag(null)
            .putShort("id", item.getId())
            .putByte("Count", item.getCount())
            .putShort("Damage", item.getDamage());
        if (slot != null) {
            tag.putByte("Slot", slot);
        }

        if (item.hasCompoundTag()) {
            tag.putCompound("tag", item.getNamedTag());
        }

        return tag;
    }

    public static Item getItemHelper(final CompoundTag tag) {
        if (!tag.contains("id") || !tag.contains("Count")) {
            return Item.get(0);
        }

        Item item;
        try {
            item = Item.get(tag.getShort("id"), !tag.contains("Damage") ? 0 : tag.getShort("Damage"), tag.getByte("Count"));
        } catch (final Exception e) {
            item = Item.fromString(tag.getString("id"));
            item.setDamage(!tag.contains("Damage") ? 0 : tag.getShort("Damage"));
            item.setCount(tag.getByte("Count"));
        }

        final Tag tagTag = tag.get("tag");
        if (tagTag instanceof CompoundTag) {
            item.setNamedTag((CompoundTag) tagTag);
        }

        return item;
    }

    public static CompoundTag read(final File file) throws IOException {
        return NBTIO.read(file, ByteOrder.BIG_ENDIAN);
    }

    public static CompoundTag read(final File file, final ByteOrder endianness) throws IOException {
        if (!file.exists()) {
            return null;
        }
        return NBTIO.read(new FileInputStream(file), endianness);
    }

    public static CompoundTag read(final InputStream inputStream) throws IOException {
        return NBTIO.read(inputStream, ByteOrder.BIG_ENDIAN);
    }

    public static CompoundTag read(final InputStream inputStream, final ByteOrder endianness) throws IOException {
        return NBTIO.read(inputStream, endianness, false);
    }

    public static CompoundTag read(final InputStream inputStream, final ByteOrder endianness, final boolean network) throws IOException {
        try (final NBTInputStream stream = new NBTInputStream(inputStream, endianness, network)) {
            final Tag tag = Tag.readNamedTag(stream);
            if (tag instanceof CompoundTag) {
                return (CompoundTag) tag;
            }
            throw new IOException("Root tag must be a named compound tag");
        }
    }

    public static Tag readTag(final InputStream inputStream, final ByteOrder endianness, final boolean network) throws IOException {
        try (final NBTInputStream stream = new NBTInputStream(inputStream, endianness, network)) {
            return Tag.readNamedTag(stream);
        }
    }

    public static CompoundTag read(final byte[] data) throws IOException {
        return NBTIO.read(data, ByteOrder.BIG_ENDIAN);
    }

    public static CompoundTag read(final byte[] data, final ByteOrder endianness) throws IOException {
        return NBTIO.read(new ByteArrayInputStream(data), endianness);
    }

    public static CompoundTag read(final byte[] data, final ByteOrder endianness, final boolean network) throws IOException {
        return NBTIO.read(new ByteArrayInputStream(data), endianness, network);
    }

    public static CompoundTag readCompressed(final InputStream inputStream) throws IOException {
        return NBTIO.readCompressed(inputStream, ByteOrder.BIG_ENDIAN);
    }

    public static CompoundTag readCompressed(final InputStream inputStream, final ByteOrder endianness) throws IOException {
        return NBTIO.read(new BufferedInputStream(new GZIPInputStream(inputStream)), endianness);
    }

    public static CompoundTag readCompressed(final byte[] data) throws IOException {
        return NBTIO.readCompressed(data, ByteOrder.BIG_ENDIAN);
    }

    public static CompoundTag readCompressed(final byte[] data, final ByteOrder endianness) throws IOException {
        return NBTIO.read(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(data))), endianness, true);
    }

    public static CompoundTag readNetworkCompressed(final InputStream inputStream) throws IOException {
        return NBTIO.readNetworkCompressed(inputStream, ByteOrder.BIG_ENDIAN);
    }

    public static CompoundTag readNetworkCompressed(final InputStream inputStream, final ByteOrder endianness) throws IOException {
        return NBTIO.read(new BufferedInputStream(new GZIPInputStream(inputStream)), endianness);
    }

    public static CompoundTag readNetworkCompressed(final byte[] data) throws IOException {
        return NBTIO.readNetworkCompressed(data, ByteOrder.BIG_ENDIAN);
    }

    public static CompoundTag readNetworkCompressed(final byte[] data, final ByteOrder endianness) throws IOException {
        return NBTIO.read(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(data))), endianness, true);
    }

    public static byte[] write(final CompoundTag tag) throws IOException {
        return NBTIO.write(tag, ByteOrder.BIG_ENDIAN);
    }

    public static byte[] write(final CompoundTag tag, final ByteOrder endianness) throws IOException {
        return NBTIO.write(tag, endianness, false);
    }

    public static byte[] write(final CompoundTag tag, final ByteOrder endianness, final boolean network) throws IOException {
        return NBTIO.write((Tag) tag, endianness, network);
    }

    public static byte[] write(final Tag tag, final ByteOrder endianness, final boolean network) throws IOException {
        final FastByteArrayOutputStream baos = ThreadCache.fbaos.get().reset();
        try (final NBTOutputStream stream = new NBTOutputStream(baos, endianness, network)) {
            Tag.writeNamedTag(tag, stream);
            return baos.toByteArray();
        }
    }

    public static byte[] write(final Collection<CompoundTag> tags) throws IOException {
        return NBTIO.write(tags, ByteOrder.BIG_ENDIAN);
    }

    public static byte[] write(final Collection<CompoundTag> tags, final ByteOrder endianness) throws IOException {
        return NBTIO.write(tags, endianness, false);
    }

    public static byte[] write(final Collection<CompoundTag> tags, final ByteOrder endianness, final boolean network) throws IOException {
        final FastByteArrayOutputStream baos = ThreadCache.fbaos.get().reset();
        try (final NBTOutputStream stream = new NBTOutputStream(baos, endianness, network)) {
            for (final CompoundTag tag : tags) {
                Tag.writeNamedTag(tag, stream);
            }
            return baos.toByteArray();
        }
    }

    public static void write(final CompoundTag tag, final File file) throws IOException {
        NBTIO.write(tag, file, ByteOrder.BIG_ENDIAN);
    }

    public static void write(final CompoundTag tag, final File file, final ByteOrder endianness) throws IOException {
        NBTIO.write(tag, new FileOutputStream(file), endianness);
    }

    public static void write(final CompoundTag tag, final OutputStream outputStream) throws IOException {
        NBTIO.write(tag, outputStream, ByteOrder.BIG_ENDIAN);
    }

    public static void write(final CompoundTag tag, final OutputStream outputStream, final ByteOrder endianness) throws IOException {
        NBTIO.write(tag, outputStream, endianness, false);
    }

    public static void write(final CompoundTag tag, final OutputStream outputStream, final ByteOrder endianness, final boolean network) throws IOException {
        try (final NBTOutputStream stream = new NBTOutputStream(outputStream, endianness, network)) {
            Tag.writeNamedTag(tag, stream);
        }
    }

    public static byte[] writeNetwork(final Tag tag) throws IOException {
        final FastByteArrayOutputStream baos = ThreadCache.fbaos.get().reset();
        try (final NBTOutputStream stream = new NBTOutputStream(baos, ByteOrder.LITTLE_ENDIAN, true)) {
            Tag.writeNamedTag(tag, stream);
        }
        return baos.toByteArray();
    }

    public static byte[] writeGZIPCompressed(final CompoundTag tag) throws IOException {
        return NBTIO.writeGZIPCompressed(tag, ByteOrder.BIG_ENDIAN);
    }

    public static byte[] writeGZIPCompressed(final CompoundTag tag, final ByteOrder endianness) throws IOException {
        final FastByteArrayOutputStream baos = ThreadCache.fbaos.get().reset();
        NBTIO.writeGZIPCompressed(tag, baos, endianness);
        return baos.toByteArray();
    }

    public static void writeGZIPCompressed(final CompoundTag tag, final OutputStream outputStream) throws IOException {
        NBTIO.writeGZIPCompressed(tag, outputStream, ByteOrder.BIG_ENDIAN);
    }

    public static void writeGZIPCompressed(final CompoundTag tag, final OutputStream outputStream, final ByteOrder endianness) throws IOException {
        NBTIO.write(tag, new PGZIPOutputStream(outputStream), endianness);
    }

    public static byte[] writeNetworkGZIPCompressed(final CompoundTag tag) throws IOException {
        return NBTIO.writeNetworkGZIPCompressed(tag, ByteOrder.BIG_ENDIAN);
    }

    public static byte[] writeNetworkGZIPCompressed(final CompoundTag tag, final ByteOrder endianness) throws IOException {
        final FastByteArrayOutputStream baos = ThreadCache.fbaos.get().reset();
        NBTIO.writeNetworkGZIPCompressed(tag, baos, endianness);
        return baos.toByteArray();
    }

    public static void writeNetworkGZIPCompressed(final CompoundTag tag, final OutputStream outputStream) throws IOException {
        NBTIO.writeNetworkGZIPCompressed(tag, outputStream, ByteOrder.BIG_ENDIAN);
    }

    public static void writeNetworkGZIPCompressed(final CompoundTag tag, final OutputStream outputStream, final ByteOrder endianness) throws IOException {
        NBTIO.write(tag, new PGZIPOutputStream(outputStream), endianness, true);
    }

    public static void writeZLIBCompressed(final CompoundTag tag, final OutputStream outputStream) throws IOException {
        NBTIO.writeZLIBCompressed(tag, outputStream, ByteOrder.BIG_ENDIAN);
    }

    public static void writeZLIBCompressed(final CompoundTag tag, final OutputStream outputStream, final ByteOrder endianness) throws IOException {
        NBTIO.writeZLIBCompressed(tag, outputStream, Deflater.DEFAULT_COMPRESSION, endianness);
    }

    public static void writeZLIBCompressed(final CompoundTag tag, final OutputStream outputStream, final int level) throws IOException {
        NBTIO.writeZLIBCompressed(tag, outputStream, level, ByteOrder.BIG_ENDIAN);
    }

    public static void writeZLIBCompressed(final CompoundTag tag, final OutputStream outputStream, final int level, final ByteOrder endianness) throws IOException {
        NBTIO.write(tag, new DeflaterOutputStream(outputStream, new Deflater(level)), endianness);
    }

    public static void safeWrite(final CompoundTag tag, final File file) throws IOException {
        final File tmpFile = new File(file.getAbsolutePath() + "_tmp");
        if (tmpFile.exists()) {
            tmpFile.delete();
        }
        NBTIO.write(tag, tmpFile);
        Files.move(tmpFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
    }

}
