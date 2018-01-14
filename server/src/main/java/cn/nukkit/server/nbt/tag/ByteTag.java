package cn.nukkit.server.nbt.tag;

import java.util.Objects;

public class ByteTag extends Tag<Byte> {
    private final byte value;

    public ByteTag(String name, byte value) {
        super(name);
        this.value = value;
    }

    public byte getPrimitiveValue() {
        return value;
    }

    @Override
    public Byte getValue() {
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
        ByteTag that = (ByteTag) o;
        return value == that.value &&
                Objects.equals(getName(), that.getName());
    }

    @Override
    public String toString() {
        return "TAG_Byte_Array" + super.toString() + value;
    }
}
