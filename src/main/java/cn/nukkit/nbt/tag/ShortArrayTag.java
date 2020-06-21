package cn.nukkit.nbt.tag;

import cn.nukkit.nbt.stream.NBTInputStream;
import cn.nukkit.nbt.stream.NBTOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class ShortArrayTag extends Tag {

    public short[] data;

    public ShortArrayTag(final String name) {
        super(name);
    }

    public ShortArrayTag(final String name, final short[] data) {
        super(name);
        this.data = data;
    }

    public short[] getData() {
        return this.data;
    }

    @Override
    public byte getId() {
        return Tag.TAG_Short_Array;
    }

    @Override
    public boolean equals(final Object obj) {
        if (super.equals(obj)) {
            final ShortArrayTag shortArrayTag = (ShortArrayTag) obj;
            return this.data == null && shortArrayTag.data == null || this.data != null && Arrays.equals(this.data, shortArrayTag.data);
        }
        return false;
    }

    @Override
    public String toString() {
        return "ShortArrayTag " + this.getName() + " [" + this.data.length + " bytes]";
    }

    @Override
    public Tag copy() {
        final short[] cp = new short[this.data.length];
        System.arraycopy(this.data, 0, cp, 0, this.data.length);
        return new ShortArrayTag(this.getName(), cp);
    }

    @Override
    public short[] parseValue() {
        return this.data;
    }

    @Override
    void write(final NBTOutputStream dos) throws IOException {
        dos.writeInt(this.data.length);
        for (final int aData : this.data) {
            dos.writeShort(aData);
        }
    }

    @Override
    void load(final NBTInputStream dis) throws IOException {
        final int length = dis.readInt();
        this.data = new short[length];
        for (int i = 0; i < length; i++) {
            this.data[i] = dis.readShort();
        }
    }

}