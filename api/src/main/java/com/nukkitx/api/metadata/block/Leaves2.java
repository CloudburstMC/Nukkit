package com.nukkitx.api.metadata.block;

import com.google.common.base.Preconditions;
import com.nukkitx.api.metadata.Wood;
import com.nukkitx.api.metadata.data.TreeSpecies;

import javax.annotation.Nonnull;

public class Leaves2 extends Wood {
    protected Leaves2(TreeSpecies species) {
        super(species);
    }

    public static Leaves2 of(@Nonnull TreeSpecies species) {
        Preconditions.checkNotNull(species, "species");
        Preconditions.checkArgument(species == TreeSpecies.ACACIA || species == TreeSpecies.DARK_OAK, "Invalid tree species. Use Leaves");
        return new Leaves2(species);
    }

    @Override
    public String toString() {
        return "Leaves2(" +
                "species=" + getSpecies() +
                ')';
    }
}