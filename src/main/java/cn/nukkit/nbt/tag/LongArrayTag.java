package cn.nukkit.nbt.tag;

import cn.nukkit.nbt.stream.NBTInputStream;
import cn.nukkit.nbt.stream.NBTOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class LongArrayTag extends Tag {

    public long[] data;

    public LongArrayTag(String name) {
        super(name);
    }

    public LongArrayTag(String name, long[] data) {
        super(name);
        this.data = data;
    }

    @Override
    void write(NBTOutputStream dos) throws IOException {
        dos.writeInt(data.length);
        for (long aData : data) {
            dos.writeLong(aData);
        }
    }

    @Override
    void load(NBTInputStream dis) throws IOException {
        int length = dis.readInt();
        data = new long[length];
        for (int i = 0; i < length; i++) {
            data[i] = dis.readLong();
        }
    }

    @Override
    public byte getId() {
        return TAG_Long_Array;
    }

    @Override
    public String toString() {
        return "LongArrayTag " + this.getName() + " [" + data.length + " bytes]";
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            LongArrayTag longArrayTag = (LongArrayTag) obj;
            return ((data == null && longArrayTag.data == null) || (data != null && Arrays.equals(data, longArrayTag.data)));
        }
        return false;
    }

    @Override
    public Tag copy() {
        long[] cp = new long[data.length];
        System.arraycopy(data, 0, cp, 0, data.length);
        return new LongArrayTag(getName(), cp);
    }
}
