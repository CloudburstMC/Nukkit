package cn.nukkit.server.nbt.stream;

import cn.nukkit.server.nbt.NBTEncodingType;
import cn.nukkit.server.nbt.TagType;
import cn.nukkit.server.nbt.tag.*;
import cn.nukkit.server.nbt.util.VarInt;

import java.io.Closeable;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static cn.nukkit.server.nbt.NBTIO.MAX_DEPTH;

public class NBTOutputStream implements Closeable {
    private final DataOutput output;
    private final NBTEncodingType encoding;
    private boolean closed = false;

    public NBTOutputStream(DataOutput output) {
        this(output, NBTEncodingType.NOTCHIAN);
    }

    public NBTOutputStream(DataOutput output, NBTEncodingType encoding) {
        this.output = Objects.requireNonNull(output, "output");
        this.encoding = Objects.requireNonNull(encoding, "encoding");
    }

    public void write(Tag<?> tag) throws IOException {
        if (closed) {
            throw new IllegalStateException("closed");
        }
        Objects.requireNonNull(tag, "tag");
        if (!(tag instanceof CompoundTag)) {
            throw new IllegalArgumentException("Trying to write a non-compound tag!");
        }
        serialize(tag, false, 0);
    }

    private void serialize(Tag<?> tag, boolean skipHeader, int depth) throws IOException {
        if (depth >= MAX_DEPTH) {
            throw new IllegalArgumentException("Reached depth limit");
        }
        TagType type = TagType.byClass(tag.getClass());
        if (type == null) {
            throw new IllegalArgumentException("Tag " + tag + " is not valid.");
        }

        if (!skipHeader) {
            output.writeByte(type.ordinal() & 0xFF);
            writeString(tag.getName());
        }

        switch (type) {
            case END:
                break;
            case BYTE:
                ByteTag bt = (ByteTag) tag;
                output.writeByte(bt.getPrimitiveValue());
                break;
            case SHORT:
                ShortTag st = (ShortTag) tag;
                output.writeShort(st.getPrimitiveValue());
                break;
            case INT:
                IntTag it = (IntTag) tag;
                if (encoding == NBTEncodingType.BEDROCK) {
                    VarInt.writeSignedInt(output, it.getPrimitiveValue());
                } else {
                    output.writeInt(it.getPrimitiveValue());
                }
                break;
            case LONG:
                LongTag lt = (LongTag) tag;
                output.writeLong(lt.getPrimitiveValue());
                break;
            case FLOAT:
                FloatTag ft = (FloatTag) tag;
                output.writeFloat(ft.getPrimitiveValue());
                break;
            case DOUBLE:
                DoubleTag dt = (DoubleTag) tag;
                output.writeDouble(dt.getPrimitiveValue());
                break;
            case BYTE_ARRAY:
                ByteArrayTag bat = (ByteArrayTag) tag;
                byte[] bValue = bat.getValue();
                if (encoding == NBTEncodingType.BEDROCK) {
                    VarInt.writeSignedInt(output, bValue.length);
                } else {
                    output.writeInt(bValue.length);
                }
                output.write(bValue);
                break;
            case STRING:
                StringTag strt = (StringTag) tag;
                writeString(strt.getValue());
                break;
            case LIST:
                ListTag<?> listt = (ListTag<?>) tag;
                output.writeByte(TagType.byClass(listt.getTagClass()).ordinal());
                if (encoding == NBTEncodingType.BEDROCK) {
                    VarInt.writeSignedInt(output, listt.getValue().size());
                } else {
                    output.writeInt(listt.getValue().size());
                }
                for (Tag<?> tag1 : listt.getValue()) {
                    serialize(tag1, true, depth + 1);
                }
                break;
            case COMPOUND:
                CompoundTag compoundTag = (CompoundTag) tag;
                for (Tag<?> tag1 : compoundTag.getValue().values()) {
                    serialize(tag1, false, depth + 1);
                }
                output.writeByte(0);
                break;
            case INT_ARRAY:
                IntArrayTag iat = (IntArrayTag) tag;
                int[] iValue = iat.getValue();
                if (encoding == NBTEncodingType.BEDROCK) {
                    VarInt.writeSignedInt(output, iValue.length);
                } else {
                    output.writeInt(iValue.length);
                }
                for (int i : iValue) {
                    output.writeInt(i);
                }
                break;
        }
    }

    private void writeString(String name) throws IOException {
        byte[] out = name.getBytes(StandardCharsets.UTF_8);
        if (encoding == NBTEncodingType.BEDROCK) {
            output.writeByte(out.length & 0xFF);
        } else {
            output.writeShort(out.length);
        }
        output.write(out);
    }

    @Override
    public void close() throws IOException {
        if (closed) return;
        closed = true;
        if (output instanceof Closeable) {
            ((Closeable) output).close();
        }
    }
}
