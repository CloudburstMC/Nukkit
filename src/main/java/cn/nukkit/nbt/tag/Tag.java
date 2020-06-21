package cn.nukkit.nbt.tag;

import cn.nukkit.nbt.stream.NBTInputStream;
import cn.nukkit.nbt.stream.NBTOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Optional;

public abstract class Tag {

    public static final byte TAG_End = 0;

    public static final byte TAG_Byte = 1;

    public static final byte TAG_Short = 2;

    public static final byte TAG_Int = 3;

    public static final byte TAG_Long = 4;

    public static final byte TAG_Float = 5;

    public static final byte TAG_Double = 6;

    public static final byte TAG_Byte_Array = 7;

    public static final byte TAG_String = 8;

    public static final byte TAG_List = 9;

    public static final byte TAG_Compound = 10;

    public static final byte TAG_Int_Array = 11;

    public static final byte TAG_Long_Array = 12;

    public static final byte TAG_Short_Array = 13;

    private String name;

    protected Tag(final String name) {
        if (name == null) {
            this.name = "";
        } else {
            this.name = name;
        }
    }

    public static Tag readNamedTag(final NBTInputStream dis) throws IOException {
        final byte type = dis.readByte();
        if (type == 0) {
            return new EndTag();
        }

        final String name = dis.readUTF();

        final Tag tag = Tag.newTag(type, name);

        tag.load(dis);
        return tag;
    }

    public static void writeNamedTag(final Tag tag, final NBTOutputStream dos) throws IOException {
        Tag.writeNamedTag(tag, tag.getName(), dos);
    }

    public static void writeNamedTag(final Tag tag, final String name, final NBTOutputStream dos) throws IOException {
        dos.writeByte(tag.getId());
        if (tag.getId() == Tag.TAG_End) {
            return;
        }
        dos.writeUTF(name);

        tag.write(dos);
    }

    public static Tag newTag(final byte type, final String name) {
        switch (type) {
            case Tag.TAG_End:
                return new EndTag();
            case Tag.TAG_Byte:
                return new ByteTag(name);
            case Tag.TAG_Short:
                return new ShortTag(name);
            case Tag.TAG_Int:
                return new IntTag(name);
            case Tag.TAG_Long:
                return new LongTag(name);
            case Tag.TAG_Float:
                return new FloatTag(name);
            case Tag.TAG_Double:
                return new DoubleTag(name);
            case Tag.TAG_Byte_Array:
                return new ByteArrayTag(name);
            case Tag.TAG_Int_Array:
                return new IntArrayTag(name);
            case Tag.TAG_String:
                return new StringTag(name);
            case Tag.TAG_List:
                return new ListTag<>(name);
            case Tag.TAG_Compound:
                return new CompoundTag(name);
        }
        return new EndTag();
    }

    public static String getTagName(final byte type) {
        switch (type) {
            case Tag.TAG_End:
                return "TAG_End";
            case Tag.TAG_Byte:
                return "TAG_Byte";
            case Tag.TAG_Short:
                return "TAG_Short";
            case Tag.TAG_Int:
                return "TAG_Int";
            case Tag.TAG_Long:
                return "TAG_Long";
            case Tag.TAG_Float:
                return "TAG_Float";
            case Tag.TAG_Double:
                return "TAG_Double";
            case Tag.TAG_Byte_Array:
                return "TAG_Byte_Array";
            case Tag.TAG_Int_Array:
                return "TAG_Int_Array";
            case Tag.TAG_String:
                return "TAG_String";
            case Tag.TAG_List:
                return "TAG_List";
            case Tag.TAG_Compound:
                return "TAG_Compound";
        }
        return "UNKNOWN";
    }

    public abstract byte getId();

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof Tag)) {
            return false;
        }
        final Tag o = (Tag) obj;
        return this.getId() == o.getId() && !(this.name == null && o.name != null || this.name != null && o.name == null) && !(this.name != null && !this.name.equals(o.name));
    }

    public abstract String toString();

    public void print(final PrintStream out) {
        this.print("", out);
    }

    public void print(final String prefix, final PrintStream out) {
        final String name = this.getName();

        out.print(prefix);
        out.print(Tag.getTagName(this.getId()));
        if (name.length() > 0) {
            out.print("(\"" + name + "\")");
        }
        out.print(": ");
        out.println(this.toString());
    }

    public String getName() {
        if (this.name == null) {
            return "";
        }
        return this.name;
    }

    public Tag setName(final String name) {
        if (name == null) {
            this.name = "";
        } else {
            this.name = name;
        }
        return this;
    }

    public abstract Tag copy();

    public abstract Object parseValue();

    public Optional<EndTag> getAsEndTag() {
        return Optional.empty();
    }

    public Optional<ByteTag> getAsByteTag() {
        return Optional.empty();
    }

    public Optional<ShortTag> getAsShortTag() {
        return Optional.empty();
    }

    public Optional<IntTag> getAsIntTag() {
        return Optional.empty();
    }

    public Optional<LongTag> getAsLongTag() {
        return Optional.empty();
    }

    public Optional<FloatTag> getAsFloatTag() {
        return Optional.empty();
    }

    public Optional<DoubleTag> getAsDoubleTag() {
        return Optional.empty();
    }

    public Optional<ByteArrayTag> getAsByteArrayTag() {
        return Optional.empty();
    }

    public Optional<StringTag> getAsStringTag() {
        return Optional.empty();
    }

    public Optional<ListTag<?>> getAsListTag() {
        return Optional.empty();
    }

    public Optional<CompoundTag> getAsCompoundTag() {
        return Optional.empty();
    }

    public Optional<IntArrayTag> getAsIntArrayTag() {
        return Optional.empty();
    }

    public Optional<LongArrayTag> getAsLongArrayTag() {
        return Optional.empty();
    }

    public Optional<ShortArrayTag> getAsShortArrayTag() {
        return Optional.empty();
    }

    abstract void write(NBTOutputStream dos) throws IOException;

    abstract void load(NBTInputStream dis) throws IOException;

}
