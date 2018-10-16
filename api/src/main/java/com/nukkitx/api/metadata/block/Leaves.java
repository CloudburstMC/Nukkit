package com.nukkitx.api.metadata.block;

import com.google.common.base.Preconditions;
import com.nukkitx.api.metadata.Wood;
import com.nukkitx.api.metadata.data.TreeSpecies;
import lombok.Getter;

import javax.annotation.Nonnull;

public class Leaves extends Wood {

    @Getter
    private final DecayState decayState;

    protected Leaves(TreeSpecies species, DecayState state) {
        super(species);
        this.decayState = state;
    }

    public static Leaves of(@Nonnull TreeSpecies species, DecayState state) {
        Preconditions.checkNotNull(species, "species");
        Preconditions.checkNotNull(state, "decay");
        Preconditions.checkArgument(species != TreeSpecies.ACACIA && species != TreeSpecies.DARK_OAK, "Invalid tree species. Use Leaves2");
        return new Leaves(species, state);
    }

    @Override
    public String toString() {
        return "Leaves(" +
                "species=" + getSpecies() +
                ", decay=" + decayState.name() +
                ')';
    }

    public enum DecayState {
        DECAY,
        NO_DECAY,
        CHECK_DECAY,
        NO_CHECK_DECAY
    }
}
