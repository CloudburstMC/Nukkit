package cn.nukkit.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class FloatTag extends Tag {
    public float data;

    public FloatTag(String name) {
        super(name);
    }

    public FloatTag(String name, float data) {
        super(name);
        this.data = data;
    }

    void write(DataOutput dos) throws IOException {
        dos.writeFloat(data);
    }

    void load(DataInput dis) throws IOException {
        data = dis.readFloat();
    }

    public byte getId() {
        return TAG_Float;
    }

    public String toString() {
        return "" + data;
    }

    @Override
    public Tag copy() {
        return new FloatTag(getName(), data);
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            FloatTag o = (FloatTag) obj;
            return data == o.data;
        }
        return false;
    }

}
