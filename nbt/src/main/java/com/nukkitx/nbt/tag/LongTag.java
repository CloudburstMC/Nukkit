package com.nukkitx.nbt.tag;

import java.util.Objects;

public class LongTag extends Tag<Long> {
    private final long value;

    public LongTag(String name, long value) {
        super(name);
        this.value = value;
    }

    public long getPrimitiveValue() {
        return value;
    }

    @Override
    public Long getValue() {
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
        LongTag that = (LongTag) o;
        return value == that.value &&
                Objects.equals(getName(), that.getName());
    }

    @Override
    public String toString() {
        return "TAG_Long" + super.toString() + value;
    }
}
