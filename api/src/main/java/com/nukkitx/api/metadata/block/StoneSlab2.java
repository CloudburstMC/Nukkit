package com.nukkitx.api.metadata.block;

import com.google.common.base.Objects;
import com.nukkitx.api.metadata.data.StoneSlabType2;
import lombok.Getter;

import javax.annotation.Nonnull;

/**
 * @author CreeperFace
 */
public class StoneSlab2 extends Slab {

    @Getter
    final StoneSlabType2 type;

    StoneSlab2(StoneSlabType2 type, boolean upper, boolean isDouble) {
        super(upper, isDouble);
        this.type = type;
    }

    public static StoneSlab2 of(@Nonnull StoneSlabType2 type, boolean upper, boolean isDouble) {
        return new StoneSlab2(type, upper, isDouble);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(type, upper, doubleSlab);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StoneSlab2 that = (StoneSlab2) o;
        return this.type == that.type && this.upper == that.upper && this.doubleSlab == that.doubleSlab;
    }

    @Override
    public String toString() {
        return "StoneSlab2(" +
                "type=" + type.name() +
                "upper=" + upper +
                "double=" + doubleSlab +
                ')';
    }
}
