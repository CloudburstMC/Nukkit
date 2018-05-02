package com.nukkitx.api.metadata.block;

import com.google.common.base.Preconditions;
import com.nukkitx.api.metadata.Wood;
import com.nukkitx.api.metadata.data.TreeSpecies;

import javax.annotation.Nonnull;
import java.util.Objects;

public class Sapling extends Wood {
    private final boolean readyToGrow;

    private Sapling(TreeSpecies species, boolean readyToGrow) {
        super(species);
        this.readyToGrow = readyToGrow;
    }

    public static Sapling of(@Nonnull TreeSpecies species, boolean readyToGrow) {
        Preconditions.checkNotNull(species, "species");
        return new Sapling(species, readyToGrow);
    }

    public boolean isReadyToGrow() {
        return readyToGrow;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSpecies(), readyToGrow);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sapling that = (Sapling) o;
        return this.getSpecies() == that.getSpecies() && this.readyToGrow == that.readyToGrow;
    }

    @Override
    public String toString() {
        return "Sapling(" +
                "species=" + getSpecies() +
                ", isReadyToGrow=" + readyToGrow +
                ')';
    }
}
