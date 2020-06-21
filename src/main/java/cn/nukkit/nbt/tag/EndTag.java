package cn.nukkit.nbt.tag;

import cn.nukkit.nbt.stream.NBTInputStream;
import cn.nukkit.nbt.stream.NBTOutputStream;
import java.io.IOException;

public class EndTag extends Tag {

    public EndTag() {
        super(null);
    }

    @Override
    public byte getId() {
        return Tag.TAG_End;
    }

    @Override
    public String toString() {
        return "EndTag";
    }

    @Override
    public Tag copy() {
        return new EndTag();
    }

    @Override
    public Object parseValue() {
        return null;
    }

    @Override
    void write(final NBTOutputStream dos) throws IOException {
    }

    @Override
    void load(final NBTInputStream dis) throws IOException {
    }

}
