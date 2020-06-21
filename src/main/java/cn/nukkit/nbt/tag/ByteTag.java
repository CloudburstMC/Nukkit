package cn.nukkit.nbt.tag;

import cn.nukkit.nbt.stream.NBTInputStream;
import cn.nukkit.nbt.stream.NBTOutputStream;
import java.io.IOException;

public class ByteTag extends NumberTag<Integer> {

    public int data;

    public ByteTag(final String name) {
        super(name);
    }

    public ByteTag(final String name, final int data) {
        super(name);
        this.data = data;
    }

    @Override
    public Integer getData() {
        return this.data;
    }

    @Override
    public void setData(final Integer data) {
        this.data = data == null ? 0 : data;
    }

    @Override
    public byte getId() {
        return Tag.TAG_Byte;
    }

    @Override
    public boolean equals(final Object obj) {
        if (super.equals(obj)) {
            final ByteTag byteTag = (ByteTag) obj;
            return this.data == byteTag.data;
        }
        return false;
    }

    @Override
    public String toString() {
        String hex = Integer.toHexString(this.data);
        if (hex.length() < 2) {
            hex = "0" + hex;
        }
        return "ByteTag " + this.getName() + " (data: 0x" + hex + ")";
    }

    @Override
    public Tag copy() {
        return new ByteTag(this.getName(), this.data);
    }

    @Override
    public Integer parseValue() {
        return this.data;
    }

    @Override
    void write(final NBTOutputStream dos) throws IOException {
        dos.writeByte(this.data);
    }

    @Override
    void load(final NBTInputStream dis) throws IOException {
        this.data = dis.readByte();
    }

}
