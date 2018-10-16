package com.nukkitx.api.metadata.block;

import com.google.common.base.Preconditions;
import com.nukkitx.api.metadata.Metadata;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Liquid implements Metadata {

    private final byte level;
    private final boolean falling;

    public static Liquid of(int level, boolean falling) {
        Preconditions.checkArgument(level >= 0 && level < 8, "level is not valid (wanted 0-7)");
        return new Liquid((byte) level, falling);
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
