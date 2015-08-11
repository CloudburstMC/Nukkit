package cn.nukkit.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class IntArrayTag extends Tag {
    public int[] data;

    public IntArrayTag(String name) {
        super(name);
    }

    public IntArrayTag(String name, int[] data) {
        super(name);
        this.data = data;
    }

    void write(DataOutput dos) throws IOException {
        dos.writeInt(data.length);
        for (int aData : data) {
            dos.writeInt(aData);
        }
    }

    void load(DataInput dis) throws IOException {
        int length = dis.readInt();
        data = new int[length];
        for (int i = 0; i < length; i++) {
            data[i] = dis.readInt();
        }
    }

    public byte getId() {
        return TAG_Int_Array;
    }

    public String toString() {
        return "[" + data.length + " bytes]";
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            IntArrayTag o = (IntArrayTag) obj;
            return ((data == null && o.data == null) || (data != null && data.equals(o.data)));
        }
        return false;
    }

    @Override
    public Tag copy() {
        int[] cp = new int[data.length];
        System.arraycopy(data, 0, cp, 0, data.length);
        return new IntArrayTag(getName(), cp);
    }
}
