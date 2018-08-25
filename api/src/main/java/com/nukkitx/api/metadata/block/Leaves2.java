package com.nukkitx.api.metadata.block;

import com.google.common.base.Preconditions;
import com.nukkitx.api.metadata.data.TreeSpecies;

import javax.annotation.Nonnull;

public class Leaves2 extends Leaves {

    protected Leaves2(TreeSpecies species, DecayState state) {
        super(species, state);
    }

    public static Leaves2 of(@Nonnull TreeSpecies species, DecayState state) {
        Preconditions.checkNotNull(species, "species");
        Preconditions.checkNotNull(state, "decay");
        Preconditions.checkArgument(species == TreeSpecies.ACACIA || species == TreeSpecies.DARK_OAK, "Invalid tree species. Use Leaves");
        return new Leaves2(species, state);
    }

    @Override
    public String toString() {
        return "Leaves2(" +
                "species=" + getSpecies() +
                ", decay=" + getDecayState().name() +
                ')';
    }
}