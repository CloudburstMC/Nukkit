package cn.nukkit.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ByteTag extends Tag {
    public byte data;

    public ByteTag(String name) {
        super(name);
    }

    public ByteTag(String name, byte data) {
        super(name);
        this.data = data;
    }

    void write(DataOutput dos) throws IOException {
        dos.writeByte(data);
    }

    void load(DataInput dis) throws IOException {
        data = dis.readByte();
    }

    public byte getId() {
        return TAG_Byte;
    }

    public String toString() {
        return "" + data;
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            ByteTag o = (ByteTag) obj;
            return data == o.data;
        }
        return false;
    }

    @Override
    public Tag copy() {
        return new ByteTag(getName(), data);
    }
}
