package cn.nukkit.nbt.tag;

import cn.nukkit.nbt.stream.NBTInputStream;
import cn.nukkit.nbt.stream.NBTOutputStream;
import java.io.IOException;

public class IntTag extends NumberTag<Integer> {

    public int data;

    public IntTag(final String name) {
        super(name);
    }

    public IntTag(final String name, final int data) {
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
        return Tag.TAG_Int;
    }

    @Override
    public boolean equals(final Object obj) {
        if (super.equals(obj)) {
            final IntTag o = (IntTag) obj;
            return this.data == o.data;
        }
        return false;
    }

    @Override
    public String toString() {
        return "IntTag " + this.getName() + "(data: " + this.data + ")";
    }

    @Override
    public Tag copy() {
        return new IntTag(this.getName(), this.data);
    }

    @Override
    public Integer parseValue() {
        return this.data;
    }

    @Override
    void write(final NBTOutputStream dos) throws IOException {
        dos.writeInt(this.data);
    }

    @Override
    void load(final NBTInputStream dis) throws IOException {
        this.data = dis.readInt();
    }

}
