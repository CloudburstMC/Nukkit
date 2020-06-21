package cn.nukkit.nbt.tag;

import cn.nukkit.nbt.stream.NBTInputStream;
import cn.nukkit.nbt.stream.NBTOutputStream;
import java.io.IOException;

public class LongTag extends NumberTag<Long> {

    public long data;

    public LongTag(final String name) {
        super(name);
    }

    public LongTag(final String name, final long data) {
        super(name);
        this.data = data;
    }

    @Override
    public Long getData() {
        return this.data;
    }

    @Override
    public void setData(final Long data) {
        this.data = data == null ? 0 : data;
    }

    @Override
    public byte getId() {
        return Tag.TAG_Long;
    }

    @Override
    public boolean equals(final Object obj) {
        if (super.equals(obj)) {
            final LongTag o = (LongTag) obj;
            return this.data == o.data;
        }
        return false;
    }

    @Override
    public String toString() {
        return "LongTag " + this.getName() + " (data:" + this.data + ")";
    }

    @Override
    public Tag copy() {
        return new LongTag(this.getName(), this.data);
    }

    @Override
    public Long parseValue() {
        return this.data;
    }

    @Override
    void write(final NBTOutputStream dos) throws IOException {
        dos.writeLong(this.data);
    }

    @Override
    void load(final NBTInputStream dis) throws IOException {
        this.data = dis.readLong();
    }

}
