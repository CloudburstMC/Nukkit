package cn.nukkit.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class EndTag extends Tag {

    public EndTag() {
        super(null);
    }

    void load(DataInput dis) throws IOException {
    }

    void write(DataOutput dos) throws IOException {
    }

    public byte getId() {
        return TAG_End;
    }

    public String toString() {
        return "END";
    }

    @Override
    public Tag copy() {
        return new EndTag();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

}
