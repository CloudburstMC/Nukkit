package cn.nukkit.nbt.tag;

import cn.nukkit.nbt.stream.NBTInputStream;
import cn.nukkit.nbt.stream.NBTOutputStream;

import java.io.IOException;

public class FloatTag extends NumberTag<Float> {
    public float data;

    @Override
    public Float getData() {
        return data;
    }

    @Override
    public void setData(Float data) {
        this.data = data == null ? 0 : data;
    }

    public FloatTag(String name) {
        super(name);
    }

    public FloatTag(String name, float data) {
        super(name);
        this.data = data;
    }

    @Override
    void write(NBTOutputStream dos) throws IOException {
        dos.writeFloat(data);
    }

    @Override
    void load(NBTInputStream dis) throws IOException {
        data = dis.readFloat();
    }

    @Override
    public byte getId() {
        return TAG_Float;
    }

    @Override
    public String toString() {
        return "FloatTag " + this.getName() + " (data: " + data + ")";
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
