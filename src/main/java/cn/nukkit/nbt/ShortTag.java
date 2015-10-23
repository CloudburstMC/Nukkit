package cn.nukkit.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ShortTag extends Tag {
    public int data;

    public ShortTag(String name) {
        super(name);
    }

    public ShortTag(String name, int data) {
        super(name);
        this.data = data;
    }

    void write(DataOutput dos) throws IOException {
        dos.writeShort(data);
    }

    void load(DataInput dis) throws IOException {
        data = dis.readUnsignedShort();
    }

    public byte getId() {
        return TAG_Short;
    }

    public String toString() {
        return "" + data;
    }

    @Override
    public Tag copy() {
        return new ShortTag(getName(), data);
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            ShortTag o = (ShortTag) obj;
            return data == o.data;
        }
        return false;
    }

}
