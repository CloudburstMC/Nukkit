package cn.nukkit.nbt.tag;

import cn.nukkit.nbt.stream.NBTInputStream;
import cn.nukkit.nbt.stream.NBTOutputStream;
import java.io.IOException;

public class DoubleTag extends NumberTag<Double> {

    public double data;

    public DoubleTag(final String name) {
        super(name);
    }

    public DoubleTag(final String name, final double data) {
        super(name);
        this.data = data;
    }

    @Override
    public Double getData() {
        return this.data;
    }

    @Override
    public void setData(final Double data) {
        this.data = data == null ? 0 : data;
    }

    @Override
    public byte getId() {
        return Tag.TAG_Double;
    }

    @Override
    public boolean equals(final Object obj) {
        if (super.equals(obj)) {
            final DoubleTag o = (DoubleTag) obj;
            return this.data == o.data;
        }
        return false;
    }

    @Override
    public String toString() {
        return "DoubleTag " + this.getName() + " (data: " + this.data + ")";
    }

    @Override
    public Tag copy() {
        return new DoubleTag(this.getName(), this.data);
    }

    @Override
    public Double parseValue() {
        return this.data;
    }

    @Override
    void write(final NBTOutputStream dos) throws IOException {
        dos.writeDouble(this.data);
    }

    @Override
    void load(final NBTInputStream dis) throws IOException {
        this.data = dis.readDouble();
    }

}
