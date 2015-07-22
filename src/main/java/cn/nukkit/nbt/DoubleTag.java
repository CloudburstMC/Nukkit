package cn.nukkit.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class DoubleTag extends Tag {
    public double data;

    public DoubleTag(String name) {
        super(name);
    }

    public DoubleTag(String name, double data) {
        super(name);
        this.data = data;
    }

    void write(DataOutput dos) throws IOException {
        dos.writeDouble(data);
    }

    void load(DataInput dis) throws IOException {
        data = dis.readDouble();
    }

    public byte getId() {
        return TAG_Double;
    }

    public String toString() {
        return "" + data;
    }

    @Override
    public Tag copy() {
        return new DoubleTag(getName(), data);
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            DoubleTag o = (DoubleTag) obj;
            return data == o.data;
        }
        return false;
    }

}
