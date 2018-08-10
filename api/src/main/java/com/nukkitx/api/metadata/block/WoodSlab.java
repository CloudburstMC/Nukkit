package com.nukkitx.api.metadata.block;

import com.google.common.base.Objects;
import com.nukkitx.api.metadata.data.TreeSpecies;
import lombok.Getter;

import javax.annotation.Nonnull;

/**
 * @author CreeperFace
 */
public class WoodSlab extends Slab {

    @Getter
    final TreeSpecies type;

    WoodSlab(TreeSpecies type, boolean upper, boolean isDouble) {
        super(upper, isDouble);
        this.type = type;
    }

    public static WoodSlab of(@Nonnull TreeSpecies type, boolean upper, boolean isDouble) {
        return new WoodSlab(type, upper, isDouble);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(type, upper, doubleSlab);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WoodSlab that = (WoodSlab) o;
        return this.type == that.type && this.upper == that.upper && this.doubleSlab == that.doubleSlab;
    }

    @Override
    public String toString() {
        return "WoodSlab(" +
                "type=" + type.name() +
                "upper=" + upper +
                "double=" + doubleSlab +
                ')';
    }
}
