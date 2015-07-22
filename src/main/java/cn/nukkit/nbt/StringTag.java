package cn.nukkit.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class StringTag extends Tag {
    public String data;

    public StringTag(String name) {
        super(name);
    }

    public StringTag(String name, String data) {
        super(name);
        this.data = data;
        if (data == null) throw new IllegalArgumentException("Empty string not allowed");
    }

    void write(DataOutput dos) throws IOException {
        dos.writeUTF(data);
    }

    void load(DataInput dis) throws IOException {
        data = dis.readUTF();
    }

    public byte getId() {
        return TAG_String;
    }

    public String toString() {
        return "" + data;
    }

    @Override
    public Tag copy() {
        return new StringTag(getName(), data);
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            StringTag o = (StringTag) obj;
            return ((data == null && o.data == null) || (data != null && data.equals(o.data)));
        }
        return false;
    }

}
