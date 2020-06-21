package cn.nukkit.nbt.stream;

import cn.nukkit.nbt.tag.*;
import cn.nukkit.utils.VarInt;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class NBTInputStream implements DataInput, AutoCloseable {

    /**
     * Flag indicating that the given data stream is not compressed.
     */
    public static final int NO_COMPRESSION = 0;

    /**
     * Flag indicating that the given data will be compressed with the GZIP compression algorithm. This is the default compression method used
     * to compress nbt files. Chunks in Minecraft Region/Anvil files with compression method {@code 1} (see the respective format documentation)
     * will use this compression method too, although this is not actively used anymore.
     */
    public static final int GZIP_COMPRESSION = 1;

    /**
     * Flag indicating that the given data will be compressed with the ZLIB compression algorithm. This is the default compression method used
     * to compress the nbt data of the chunks in Minecraft Region/Anvil files, but only if its compression method is {@code 2} (see the
     * respective format documentation), which is default for all newer versions.
     */
    public static final int ZLIB_COMPRESSION = 2;

    private final DataInputStream stream;

    private final ByteOrder endianness;

    private final boolean network;

    public NBTInputStream(final InputStream stream) {
        this(stream, ByteOrder.BIG_ENDIAN);
    }

    public NBTInputStream(final InputStream stream, final ByteOrder endianness) {
        this(stream, endianness, false);
    }

    public NBTInputStream(final InputStream stream, final ByteOrder endianness, final boolean network) {
        this.stream = stream instanceof DataInputStream ? (DataInputStream) stream : new DataInputStream(stream);
        this.endianness = endianness;
        this.network = network;
    }

    public NBTInputStream(InputStream stream, final int compression, final ByteOrder endianness) throws IOException {
        switch (compression) {
            case NBTInputStream.NO_COMPRESSION:
                break;
            case NBTInputStream.GZIP_COMPRESSION:
                stream = new GZIPInputStream(stream);
                break;
            case NBTInputStream.ZLIB_COMPRESSION:
                stream = new InflaterInputStream(stream);
                break;
            default:
                throw new IllegalArgumentException("Unsupported compression type, must be between 0 and 2 (inclusive)");
        }
        if (stream instanceof DataInputStream) {
            this.stream = (DataInputStream) stream;
        } else {
            this.stream = new DataInputStream(stream);
        }
        this.endianness = endianness;
        this.network = false;
    }

    public ByteOrder getEndianness() {
        return this.endianness;
    }

    public boolean isNetwork() {
        return this.network;
    }

    @Override
    public void readFully(final byte[] b) throws IOException {
        this.stream.readFully(b);
    }

    @Override
    public void readFully(final byte[] b, final int off, final int len) throws IOException {
        this.stream.readFully(b, off, len);
    }

    @Override
    public int skipBytes(final int n) throws IOException {
        return this.stream.skipBytes(n);
    }

    @Override
    public boolean readBoolean() throws IOException {
        return this.stream.readBoolean();
    }

    @Override
    public byte readByte() throws IOException {
        return this.stream.readByte();
    }

    @Override
    public int readUnsignedByte() throws IOException {
        return this.stream.readUnsignedByte();
    }

    @Override
    public short readShort() throws IOException {
        short s = this.stream.readShort();
        if (this.endianness == ByteOrder.LITTLE_ENDIAN) {
            s = Short.reverseBytes(s);
        }
        return s;
    }

    @Override
    public int readUnsignedShort() throws IOException {
        return this.readShort() & 0xFFFF;
    }

    @Override
    public char readChar() throws IOException {
        char c = this.stream.readChar();
        if (this.endianness == ByteOrder.LITTLE_ENDIAN) {
            c = Character.reverseBytes(c);
        }
        return c;
    }

    @Override
    public int readInt() throws IOException {
        if (this.network) {
            return VarInt.readVarInt(this.stream);
        }
        int i = this.stream.readInt();
        if (this.endianness == ByteOrder.LITTLE_ENDIAN) {
            i = Integer.reverseBytes(i);
        }
        return i;
    }

    @Override
    public long readLong() throws IOException {
        if (this.network) {
            return VarInt.readVarLong(this.stream);
        }
        long l = this.stream.readLong();
        if (this.endianness == ByteOrder.LITTLE_ENDIAN) {
            l = Long.reverseBytes(l);
        }
        return l;
    }

    @Override
    public float readFloat() throws IOException {
        int i = this.stream.readInt();
        if (this.endianness == ByteOrder.LITTLE_ENDIAN) {
            i = Integer.reverseBytes(i);
        }
        return Float.intBitsToFloat(i);
    }

    @Override
    public double readDouble() throws IOException {
        long l = this.stream.readLong();
        if (this.endianness == ByteOrder.LITTLE_ENDIAN) {
            l = Long.reverseBytes(l);
        }
        return Double.longBitsToDouble(l);
    }

    @Override
    @Deprecated
    public String readLine() throws IOException {
        return this.stream.readLine();
    }

    @Override
    public String readUTF() throws IOException {
        final int length = this.network ? (int) VarInt.readUnsignedVarInt(this.stream) : this.readUnsignedShort();
        final byte[] bytes = new byte[length];
        this.stream.read(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public int available() throws IOException {
        return this.stream.available();
    }

    @Override
    public void close() throws IOException {
        this.stream.close();
    }

    /**
     * Reads an NBT {@link Tag} from the stream.
     *
     * @return The tag that was read.
     * @throws IOException if an I/O error occurs.
     */
    public Tag readTag() throws IOException {
        return this.readTag(0);
    }

    /**
     * Reads an NBT {@link Tag} from the stream.
     *
     * @param depth The depth of this tag.
     * @return The tag that was read.
     * @throws IOException if an I/O error occurs.
     */
    private Tag readTag(final int depth) throws IOException {
        final TagType tagType = TagType.getById(this.stream.readByte() & 0xFF);

        final String name;
        if (tagType != TagType.TAG_End) {
            name = this.stream.readUTF();
        } else {
            name = "";
        }

        return this.readTagPayload(tagType, name, depth);
    }

    /**
     * Reads the payload of a {@link Tag}, given the name and type.
     *
     * @param type The type.
     * @param name The name.
     * @param depth The depth.
     * @return The tag.
     * @throws IOException if an I/O error occurs.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private Tag readTagPayload(final TagType type, final String name, final int depth) throws IOException {
        switch (type) {
            case TAG_End:
                if (depth == 0) {
                    throw new IOException("TAG_End found without a TAG_Compound/TAG_List tag preceding it.");
                } else {
                    return new EndTag();
                }
            case TAG_Byte:
                return new ByteTag(name, this.stream.readByte());
            case TAG_Short:
                return new ShortTag(name, this.stream.readShort());
            case TAG_Int:
                return new IntTag(name, this.stream.readInt());
            case TAG_Long:
                return new LongTag(name, this.stream.readLong());
            case TAG_Float:
                return new FloatTag(name, this.stream.readFloat());
            case TAG_Double:
                return new DoubleTag(name, this.stream.readDouble());
            case TAG_Byte_Array:
                int length = this.stream.readInt();
                final byte[] bytes = new byte[length];
                this.stream.readFully(bytes);
                return new ByteArrayTag(name, bytes);
            case TAG_String:
                return new StringTag(name, this.stream.readUTF());
            case TAG_List:
                final TagType childType = TagType.getById(this.stream.readByte());
                length = this.stream.readInt();
                final Class<? extends Tag> clazz = childType.getClazz();
                final List<Tag> tagList = new ArrayList<>(length);
                for (int i = 0; i < length; i++) {
                    final Tag tag = this.readTagPayload(childType, "", depth + 1);
                    if (tag instanceof EndTag) {
                        //throw new IOException("TAG_End not permitted in a list.");
                        continue;
                    } else if (!clazz.isInstance(tag)) {
                        throw new IOException("Mixed tag types within a list.");
                    }
                    tagList.add(tag);
                }

                return new ListTag(name, tagList);
            case TAG_Compound:
                final CompoundTag compoundTag = new CompoundTag(name);
                while (true) {
                    final Tag tag = this.readTag(depth + 1);
                    if (tag instanceof EndTag) {
                        break;
                    } else {
                        compoundTag.put(tag.getName(), tag);
                    }
                }

                return compoundTag;
            case TAG_Int_Array:
                length = this.stream.readInt();
                final int[] ints = new int[length];
                for (int i = 0; i < length; i++) {
                    ints[i] = this.stream.readInt();
                }
                return new IntArrayTag(name, ints);
            case TAG_Long_Array:
                length = this.stream.readInt();
                final long[] longs = new long[length];
                for (int i = 0; i < length; i++) {
                    longs[i] = this.stream.readLong();
                }
                return new LongArrayTag(name, longs);
            case TAG_Short_Array:
                length = this.stream.readInt();
                final short[] shorts = new short[length];
                for (int i = 0; i < length; i++) {
                    shorts[i] = this.stream.readShort();
                }
                return new ShortArrayTag(name, shorts);
            default:
                throw new IOException("Invalid tag type: " + type + ".");
        }
    }

}
