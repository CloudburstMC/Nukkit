package cn.nukkit.server.nbt.tag;

import java.util.Arrays;
import java.util.Objects;

public class ByteArrayTag extends Tag<byte[]> {
    private final byte[] value;

    public ByteArrayTag(String name, byte[] value) {
        super(name);
        this.value = value;
    }

    @Override
    public byte[] getValue() {
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
        ByteArrayTag that = (ByteArrayTag) o;
        return Arrays.equals(value, that.value) &&
                Objects.equals(getName(), that.getName());
    }

    @Override
    public String toString() {
        return "TAG_Byte_Array" + super.toString() + "[" + value.length + " bytes]";
    }
}
