package com.nukkitx.nbt.tag;

import java.util.Objects;

public class DoubleTag extends Tag<Double> {
    private final double value;

    public DoubleTag(String name, double value) {
        super(name);
        this.value = value;
    }

    public double getPrimitiveValue() {
        return value;
    }

    @Override
    public Double getValue() {
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
        DoubleTag that = (DoubleTag) o;
        return value == that.value &&
                Objects.equals(getName(), that.getName());
    }

    @Override
    public String toString() {
        return "TAG_Double" + super.toString() + value;
    }
}
