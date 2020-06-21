package cn.nukkit.nbt.stream;

import cn.nukkit.nbt.tag.*;
import cn.nukkit.utils.VarInt;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class NBTOutputStream implements DataOutput, AutoCloseable {

    private final DataOutputStream stream;

    private final ByteOrder endianness;

    private final boolean network;

    public NBTOutputStream(final OutputStream stream) {
        this(stream, ByteOrder.BIG_ENDIAN);
    }

    public NBTOutputStream(final OutputStream stream, final ByteOrder endianness) {
        this(stream, endianness, false);
    }

    public NBTOutputStream(final OutputStream stream, final ByteOrder endianness, final boolean network) {
        this.stream = stream instanceof DataOutputStream ? (DataOutputStream) stream : new DataOutputStream(stream);
        this.endianness = endianness;
        this.network = network;
    }

    public ByteOrder getEndianness() {
        return this.endianness;
    }

    public boolean isNetwork() {
        return this.network;
    }

    @Override
    public void write(final int b) throws IOException {
        this.stream.write(b);
    }

    @Override
    public void write(final byte[] bytes) throws IOException {
        this.stream.write(bytes);
    }

    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        this.stream.write(b, off, len);
    }

    @Override
    public void writeBoolean(final boolean v) throws IOException {
        this.stream.writeBoolean(v);
    }

    @Override
    public void writeByte(final int v) throws IOException {
        this.stream.writeByte(v);
    }

    @Override
    public void writeShort(int v) throws IOException {
        if (this.endianness == ByteOrder.LITTLE_ENDIAN) {
            v = Integer.reverseBytes(v) >> 16;
        }
        this.stream.writeShort(v);
    }

    @Override
    public void writeChar(int v) throws IOException {
        if (this.endianness == ByteOrder.LITTLE_ENDIAN) {
            v = Character.reverseBytes((char) v);
        }
        this.stream.writeChar(v);
    }

    @Override
    public void writeInt(int v) throws IOException {
        if (this.network) {
            VarInt.writeVarInt(this.stream, v);
        } else {
            if (this.endianness == ByteOrder.LITTLE_ENDIAN) {
                v = Integer.reverseBytes(v);
            }
            this.stream.writeInt(v);
        }
    }

    @Override
    public void writeLong(long v) throws IOException {
        if (this.network) {
            VarInt.writeVarLong(this.stream, v);
        } else {
            if (this.endianness == ByteOrder.LITTLE_ENDIAN) {
                v = Long.reverseBytes(v);
            }
            this.stream.writeLong(v);
        }
    }

    @Override
    public void writeFloat(final float v) throws IOException {
        int i = Float.floatToIntBits(v);
        if (this.endianness == ByteOrder.LITTLE_ENDIAN) {
            i = Integer.reverseBytes(i);
        }
        this.stream.writeInt(i);
    }

    @Override
    public void writeDouble(final double v) throws IOException {
        long l = Double.doubleToLongBits(v);
        if (this.endianness == ByteOrder.LITTLE_ENDIAN) {
            l = Long.reverseBytes(l);
        }
        this.stream.writeLong(l);
    }

    @Override
    public void writeBytes(final String s) throws IOException {
        this.stream.writeBytes(s);
    }

    @Override
    public void writeChars(final String s) throws IOException {
        this.stream.writeChars(s);
    }

    @Override
    public void writeUTF(final String s) throws IOException {
        final byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        if (this.network) {
            VarInt.writeUnsignedVarInt(this.stream, bytes.length);
        } else {
            this.writeShort(bytes.length);
        }
        this.stream.write(bytes);
    }

    @Override
    public void close() throws IOException {
        this.stream.close();
    }

    public void writeTag(final Tag tag) throws IOException {
        final String name = tag.getName();
        this.stream.writeByte(tag.getId());
        this.stream.writeUTF(name);
        if (tag.getId() == Tag.TAG_End) {
            throw new IOException("Named TAG_End not permitted.");
        } else {
            this.writeTagPayload(tag);
        }
    }

    private void writeTagPayload(final Tag tag) throws IOException {
        switch (tag.getId()) {
            case 0:
                this.writeEndTagPayload((EndTag) tag);
                break;
            case 1:
                this.writeByteTagPayload((ByteTag) tag);
                break;
            case 2:
                this.writeShortTagPayload((ShortTag) tag);
                break;
            case 3:
                this.writeIntTagPayload((IntTag) tag);
                break;
            case 4:
                this.writeLongTagPayload((LongTag) tag);
                break;
            case 5:
                this.writeFloatTagPayload((FloatTag) tag);
                break;
            case 6:
                this.writeDoubleTagPayload((DoubleTag) tag);
                break;
            case 7:
                this.writeByteArrayTagPayload((ByteArrayTag) tag);
                break;
            case 8:
                this.writeStringTagPayload((StringTag) tag);
                break;
            case 9:
                this.writeListTagPayload((ListTag<? extends Tag>) tag);
                break;
            case 10:
                this.writeCompoundTagPayload((CompoundTag) tag);
                break;
            case 11:
                this.writeIntArrayTagPayload((IntArrayTag) tag);
                break;
            case 12:
                this.writeLongArrayTagPayload((LongArrayTag) tag);
                break;
            case 13:
                this.writeShortArrayTagPayload((ShortArrayTag) tag);
                break;
            default:
                throw new IOException("Invalid tag type: " + tag.getId() + ".");
        }

    }

    private void writeByteTagPayload(final ByteTag tag) throws IOException {
        this.stream.writeByte(tag.getData());
    }

    private void writeByteArrayTagPayload(final ByteArrayTag tag) throws IOException {
        final byte[] bytes = tag.getData();
        this.stream.writeInt(bytes.length);
        this.stream.write(bytes);
    }

    private void writeCompoundTagPayload(final CompoundTag tag) throws IOException {
        for (final Tag o : tag.getAllTags()) {
            this.writeTag(o);
        }
        this.stream.writeByte(Tag.TAG_End);
    }

    private void writeListTagPayload(final ListTag<? extends Tag> tag) throws IOException {
        final List<? extends Tag> tags = tag.getAll();
        final int size = tags.size();
        this.stream.writeByte(tag.type);
        this.stream.writeInt(size);
        for (final Tag tag1 : tags) {
            this.writeTagPayload(tag1);
        }
    }

    private void writeStringTagPayload(final StringTag tag) throws IOException {
        this.stream.writeUTF(tag.parseValue());
    }

    private void writeDoubleTagPayload(final DoubleTag tag) throws IOException {
        this.stream.writeDouble(tag.getData());
    }

    private void writeFloatTagPayload(final FloatTag tag) throws IOException {
        this.stream.writeFloat(tag.getData());
    }

    private void writeLongTagPayload(final LongTag tag) throws IOException {
        this.stream.writeLong(tag.getData());
    }

    private void writeIntTagPayload(final IntTag tag) throws IOException {
        this.stream.writeInt(tag.getData());
    }

    private void writeShortTagPayload(final ShortTag tag) throws IOException {
        this.stream.writeShort(tag.getData());
    }

    private void writeIntArrayTagPayload(final IntArrayTag tag) throws IOException {
        final int[] ints = tag.getData();
        this.stream.writeInt(ints.length);
        for (final int anInt : ints) {
            this.stream.writeInt(anInt);
        }
    }

    private void writeLongArrayTagPayload(final LongArrayTag tag) throws IOException {
        final long[] longs = tag.getData();
        this.stream.writeInt(longs.length);
        for (final long aLong : longs) {
            this.stream.writeLong(aLong);
        }
    }

    private void writeShortArrayTagPayload(final ShortArrayTag tag) throws IOException {
        final short[] shorts = tag.getData();
        this.stream.writeInt(shorts.length);
        for (final short aShort : shorts) {
            this.stream.writeShort(aShort);
        }
    }

    private void writeEndTagPayload(final EndTag tag) {
    }

}
