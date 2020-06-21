package cn.nukkit.nbt.tag;

import cn.nukkit.nbt.stream.NBTInputStream;
import cn.nukkit.nbt.stream.NBTOutputStream;
import java.io.IOException;

public class FloatTag extends NumberTag<Float> {

    public float data;

    public FloatTag(final String name) {
        super(name);
    }

    public FloatTag(final String name, final float data) {
        super(name);
        this.data = data;
    }

    @Override
    public Float getData() {
        return this.data;
    }

    @Override
    public void setData(final Float data) {
        this.data = data == null ? 0 : data;
    }

    @Override
    public byte getId() {
        return Tag.TAG_Float;
    }

    @Override
    public boolean equals(final Object obj) {
        if (super.equals(obj)) {
            final FloatTag o = (FloatTag) obj;
            return this.data == o.data;
        }
        return false;
    }

    @Override
    public String toString() {
        return "FloatTag " + this.getName() + " (data: " + this.data + ")";
    }

    @Override
    public Tag copy() {
        return new FloatTag(this.getName(), this.data);
    }

    @Override
    public Float parseValue() {
        return this.data;
    }

    @Override
    void write(final NBTOutputStream dos) throws IOException {
        dos.writeFloat(this.data);
    }

    @Override
    void load(final NBTInputStream dis) throws IOException {
        this.data = dis.readFloat();
    }

}
