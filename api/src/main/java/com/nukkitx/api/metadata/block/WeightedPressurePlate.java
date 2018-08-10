package com.nukkitx.api.metadata.block;

import com.nukkitx.api.metadata.Metadata;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

/**
 * @author CreeperFace
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class WeightedPressurePlate implements Metadata {

    @Getter
    private final int powerLevel;

    public static WeightedPressurePlate of(int powerLevel) {
        return new WeightedPressurePlate(powerLevel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(powerLevel);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WeightedPressurePlate that = (WeightedPressurePlate) o;
        return this.powerLevel == that.powerLevel;
    }

    @Override
    public String toString() {
        return "Weighted Pressure Plate(" +
                "power level=" + powerLevel +
                ')';
    }
}
