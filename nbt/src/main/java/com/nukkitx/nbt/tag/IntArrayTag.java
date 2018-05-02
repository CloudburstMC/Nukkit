package com.nukkitx.nbt.tag;

import java.util.Arrays;
import java.util.Objects;

public class IntArrayTag extends Tag<int[]> {
    private final int[] value;

    public IntArrayTag(String name, int[] value) {
        super(name);
        this.value = value;
    }

    @Override
    public int[] getValue() {
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
        IntArrayTag that = (IntArrayTag) o;
        return Arrays.equals(value, that.value) &&
                Objects.equals(getName(), that.getName());
    }

    @Override
    public String toString() {
        return "TAG_Int_Array" + super.toString() + "[" + value.length + " values]";
    }
}
