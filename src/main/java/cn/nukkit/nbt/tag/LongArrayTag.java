package cn.nukkit.nbt.tag;

import cn.nukkit.nbt.stream.NBTInputStream;
import cn.nukkit.nbt.stream.NBTOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class LongArrayTag extends Tag {

    public long[] data;

    public LongArrayTag(final String name) {
        super(name);
    }

    public LongArrayTag(final String name, final long[] data) {
        super(name);
        this.data = data;
    }

    public long[] getData() {
        return this.data;
    }

    @Override
    public byte getId() {
        return Tag.TAG_Long_Array;
    }

    @Override
    public boolean equals(final Object obj) {
        if (super.equals(obj)) {
            final LongArrayTag longArrayTag = (LongArrayTag) obj;
            return this.data == null && longArrayTag.data == null || this.data != null && Arrays.equals(this.data, longArrayTag.data);
        }
        return false;
    }

    @Override
    public String toString() {
        return "LongArrayTag " + this.getName() + " [" + this.data.length + " bytes]";
    }

    @Override
    public Tag copy() {
        final long[] cp = new long[this.data.length];
        System.arraycopy(this.data, 0, cp, 0, this.data.length);
        return new LongArrayTag(this.getName(), cp);
    }

    @Override
    public long[] parseValue() {
        return this.data;
    }

    @Override
    void write(final NBTOutputStream dos) throws IOException {
        dos.writeInt(this.data.length);
        for (final long aData : this.data) {
            dos.writeLong(aData);
        }
    }

    @Override
    void load(final NBTInputStream dis) throws IOException {
        final int length = dis.readInt();
        this.data = new long[length];
        for (int i = 0; i < length; i++) {
            this.data[i] = dis.readLong();
        }
    }

}