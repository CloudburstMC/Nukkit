package com.nukkitx.api.metadata;

import com.google.common.base.Preconditions;
import com.nukkitx.api.metadata.data.TreeSpecies;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Wood implements Metadata {

    @Getter
    private final TreeSpecies species;

    public static Wood of(TreeSpecies species) {
        Preconditions.checkNotNull(species, "species");
        return new Wood(species);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Wood that = (Wood) o;
        return this.species == that.species;
    }

    @Override
    public int hashCode() {
        return Objects.hash(species);
    }

    @Override
    public String toString() {
        return "Wood(" +
                "species=" + species +
                ')';
    }
}
