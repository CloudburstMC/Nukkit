package cn.nukkit.nbt.tag;

import cn.nukkit.nbt.stream.NBTInputStream;
import cn.nukkit.nbt.stream.NBTOutputStream;

import java.io.IOException;

public class ShortTag extends NumberTag<Integer> {
    public int data;

    @Override
    public Integer getData() {
        return data;
    }

    @Override
    public void setData(Integer data) {
        this.data = data == null ? 0 : data;
    }

    public ShortTag(String name) {
        super(name);
    }

    public ShortTag(String name, int data) {
        super(name);
        this.data = data;
    }

    @Override
    void write(NBTOutputStream dos) throws IOException {
        dos.writeShort(data);
    }

    @Override
    void load(NBTInputStream dis) throws IOException {
        data = dis.readUnsignedShort();
    }

    @Override
    public byte getId() {
        return TAG_Short;
    }

    @Override
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
