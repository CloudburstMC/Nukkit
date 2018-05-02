package com.nukkitx.api.metadata.block;

import com.google.common.base.Preconditions;
import com.nukkitx.api.metadata.Metadata;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Cake implements Metadata {
    public static final Cake NEW = new Cake((byte) 0);
    public static final Cake ALMOST_EATEN = new Cake((byte) 6);

    private final byte level;

    public static Cake of(int data) {
        Preconditions.checkArgument(data >= 0 && data < 8, "data is not valid (wanted 0-7)");
        return new Cake((byte) data);
    }

    public int getSlicesEaten() {
        return level;
    }

    public int getSlicesLeft() {
        return 7 - level;
    }

    public byte getLevel() {
        return level;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cake that = (Cake) o;
        return this.level == that.level;
    }

    @Override
    public int hashCode() {
        return level;
    }

    @Override
    public String toString() {
        return "Cake(" +
                "level=" + level +
                ')';
    }
}
