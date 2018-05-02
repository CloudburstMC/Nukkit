package com.nukkitx.api.metadata.block;

import com.google.common.base.Preconditions;
import com.nukkitx.api.metadata.Metadata;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Cauldron implements Metadata {
    public static final Cauldron EMPTY = new Cauldron((byte) 0);
    public static final Cauldron FULL = new Cauldron((byte) 3);
    private final byte level;

    public static Cauldron of(int level) {
        Preconditions.checkArgument(level >= 0 && level < 4, "Invalid level. Expected 0-3");
        return new Cauldron((byte) level);
    }

    public boolean isEmpty() {
        return level == 0;
    }

    public boolean isFull() {
        return level == 3;
    }

    public byte getLevel() {
        return level;
    }

    @Override
    public int hashCode() {
        return level;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cauldron that = (Cauldron) o;
        return this.level == that.level;
    }

    @Override
    public String toString() {
        return "Cauldron(" +
                "level=" + level +
                ')';
    }
}
