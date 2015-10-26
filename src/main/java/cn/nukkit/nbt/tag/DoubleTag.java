package cn.nukkit.nbt.tag;

import cn.nukkit.nbt.stream.NBTInputStream;
import cn.nukkit.nbt.stream.NBTOutputStream;

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

    @Override
    void write(NBTOutputStream dos) throws IOException {
        dos.writeDouble(data);
    }

    @Override
    void load(NBTInputStream dis) throws IOException {
        data = dis.readDouble();
    }

    @Override
    public byte getId() {
        return TAG_Double;
    }

    @Override
    public String toString() {
        return "DoubleTag " + this.getName() + " (data: " + data + ")";
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
