package cn.nukkit.nbt.tag;

import cn.nukkit.nbt.stream.NBTInputStream;
import cn.nukkit.nbt.stream.NBTOutputStream;
import java.io.IOException;
import java.util.Optional;

public class StringTag extends Tag {

    public String data;

    public StringTag(final String name) {
        super(name);
    }

    public StringTag(final String name, final String data) {
        super(name);
        this.data = data;
        if (data == null) {
            throw new IllegalArgumentException("Empty string not allowed");
        }
    }

    @Override
    public byte getId() {
        return Tag.TAG_String;
    }

    @Override
    public boolean equals(final Object obj) {
        if (super.equals(obj)) {
            final StringTag o = (StringTag) obj;
            return this.data == null && o.data == null || this.data != null && this.data.equals(o.data);
        }
        return false;
    }

    @Override
    public String toString() {
        return "StringTag " + this.getName() + " (data: " + this.data + ")";
    }

    @Override
    public Tag copy() {
        return new StringTag(this.getName(), this.data);
    }

    @Override
    public String parseValue() {
        return this.data;
    }

    @Override
    public Optional<StringTag> getAsStringTag() {
        return Optional.of(this);
    }

    @Override
    void write(final NBTOutputStream dos) throws IOException {
        dos.writeUTF(this.data);
    }

    @Override
    void load(final NBTInputStream dis) throws IOException {
        this.data = dis.readUTF();
    }

}
