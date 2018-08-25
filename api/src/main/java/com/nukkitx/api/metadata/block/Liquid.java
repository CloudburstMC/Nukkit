package com.nukkitx.api.metadata.block;

import com.google.common.base.Preconditions;
import com.nukkitx.api.metadata.Metadata;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Liquid implements Metadata {

    @Getter
    private final byte level;

    public static Liquid of(int level) {
        Preconditions.checkArgument(level >= 0 && level < 8, "level is not valid (wanted 0-7)");
        return new Liquid((byte) level);
    }

    @Override
    public int hashCode() {
        return level;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Liquid that = (Liquid) o;
        return this.level == that.level;
    }

    @Override
    public String toString() {
        return "Liquid(" +
                "level=" + level +
                ')';
    }
}
