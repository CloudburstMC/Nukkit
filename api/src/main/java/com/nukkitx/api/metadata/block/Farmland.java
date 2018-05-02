package com.nukkitx.api.metadata.block;

import com.google.common.base.Preconditions;
import com.nukkitx.api.metadata.Metadata;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Farmland implements Metadata {
    private final byte wetness;

    public static Farmland of(int wetness) {
        Preconditions.checkArgument(wetness >= 0 && wetness < 8, "Invalid value. Expected 0-7");
        return new Farmland((byte) wetness);
    }

    public boolean isDry() {
        return wetness == 0;
    }

    public byte getWetness() {
        return wetness;
    }

    @Override
    public int hashCode() {
        return wetness;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Farmland that = (Farmland) o;
        return this.wetness == that.wetness;
    }

    @Override
    public String toString() {
        return "Farmland(" +
                "wetness=" + wetness +
                ')';
    }
}
