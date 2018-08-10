package com.nukkitx.api.metadata.block;

import com.google.common.base.Objects;
import com.nukkitx.api.metadata.data.StoneSlabType;
import lombok.Getter;

import javax.annotation.Nonnull;

/**
 * @author CreeperFace
 */
public class StoneSlab extends Slab {

    @Getter
    final StoneSlabType type;

    StoneSlab(StoneSlabType type, boolean upper, boolean isDouble) {
        super(upper, isDouble);
        this.type = type;
    }

    public static StoneSlab of(@Nonnull StoneSlabType type, boolean upper, boolean isDouble) {
        return new StoneSlab(type, upper, isDouble);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(type, upper, doubleSlab);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StoneSlab that = (StoneSlab) o;
        return this.type == that.type && this.upper == that.upper && this.doubleSlab == that.doubleSlab;
    }

    @Override
    public String toString() {
        return "StoneSlab(" +
                "type=" + type.name() +
                "upper=" + upper +
                "double=" + doubleSlab +
                ')';
    }
}
