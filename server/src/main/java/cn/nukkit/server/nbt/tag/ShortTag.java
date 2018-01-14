package cn.nukkit.server.nbt.tag;

import java.util.Objects;

public class ShortTag extends Tag<Short> {
    private final short value;

    public ShortTag(String name, short value) {
        super(name);
        this.value = value;
    }

    public short getPrimitiveValue() {
        return value;
    }

    @Override
    public Short getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShortTag that = (ShortTag) o;
        return value == that.value &&
                Objects.equals(getName(), that.getName());
    }

    @Override
    public String toString() {
        return "TAG_Short" + super.toString() + value;
    }
}
