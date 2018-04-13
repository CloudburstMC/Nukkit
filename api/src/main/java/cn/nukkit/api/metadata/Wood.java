package cn.nukkit.api.metadata;

import cn.nukkit.api.metadata.data.TreeSpecies;
import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;

import java.util.Objects;

@AllArgsConstructor
public class Wood implements Metadata {
    private final TreeSpecies species;

    public static Wood of(TreeSpecies species) {
        Preconditions.checkNotNull(species, "species");
        return new Wood(species);
    }

    public TreeSpecies getSpecies() {
        return species;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Wood wood = (Wood) o;
        return species == wood.species;
    }

    @Override
    public int hashCode() {
        return Objects.hash(species);
    }

    @Override
    public String toString() {
        return "Wood{" +
                "species=" + species +
                '}';
    }
}
