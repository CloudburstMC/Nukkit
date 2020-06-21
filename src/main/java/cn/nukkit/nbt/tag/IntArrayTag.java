package cn.nukkit.nbt.tag;

import cn.nukkit.nbt.stream.NBTInputStream;
import cn.nukkit.nbt.stream.NBTOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class IntArrayTag extends Tag {

    public int[] data;

    public IntArrayTag(final String name) {
        super(name);
    }

    public IntArrayTag(final String name, final int[] data) {
        super(name);
        this.data = data;
    }

    public int[] getData() {
        return this.data;
    }

    @Override
    public byte getId() {
        return Tag.TAG_Int_Array;
    }

    @Override
    public boolean equals(final Object obj) {
        if (super.equals(obj)) {
            final IntArrayTag intArrayTag = (IntArrayTag) obj;
            return this.data == null && intArrayTag.data == null || this.data != null && Arrays.equals(this.data, intArrayTag.data);
        }
        return false;
    }

    @Override
    public String toString() {
        return "IntArrayTag " + this.getName() + " [" + this.data.length + " bytes]";
    }

    @Override
    public Tag copy() {
        final int[] cp = new int[this.data.length];
        System.arraycopy(this.data, 0, cp, 0, this.data.length);
        return new IntArrayTag(this.getName(), cp);
    }

    @Override
    public int[] parseValue() {
        return this.data;
    }

    @Override
    void write(final NBTOutputStream dos) throws IOException {
        dos.writeInt(this.data.length);
        for (final int aData : this.data) {
            dos.writeInt(aData);
        }
    }

    @Override
    void load(final NBTInputStream dis) throws IOException {
        final int length = dis.readInt();
        this.data = new int[length];
        for (int i = 0; i < length; i++) {
            this.data[i] = dis.readInt();
        }
    }

}
