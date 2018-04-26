package cn.nukkit.api.metadata.block;

import cn.nukkit.api.metadata.Wood;
import cn.nukkit.api.metadata.data.TreeSpecies;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;

public class Leaves extends Wood {
    protected Leaves(TreeSpecies species) {
        super(species);
    }

    public static Leaves of(@Nonnull TreeSpecies species) {
        Preconditions.checkNotNull(species, "species");
        Preconditions.checkArgument(species != TreeSpecies.ACACIA && species != TreeSpecies.DARK_OAK, "Invalid tree species. Use Leaves2");
        return new Leaves(species);
    }

    @Override
    public String toString() {
        return "Leaves(" +
                "species=" + getSpecies() +
                ')';
    }
}
