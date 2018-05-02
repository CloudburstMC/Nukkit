package com.nukkitx.api.metadata.block;

import com.nukkitx.api.metadata.Metadata;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Sand implements Metadata {
    private final boolean red;

    public static Sand of(boolean isRed) {
        return new Sand(isRed);
    }

    public boolean isRed() {
        return red;
    }

    @Override
    public int hashCode() {
        return Boolean.hashCode(red);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sand that = (Sand) o;
        return this.red == that.red;
    }

    @Override
    public String toString() {
        return "Sand(" +
                "isRed=" + red +
                ')';
    }
}
