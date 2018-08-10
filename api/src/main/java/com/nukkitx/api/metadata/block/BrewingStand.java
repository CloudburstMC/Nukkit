package com.nukkitx.api.metadata.block;

import com.nukkitx.api.metadata.Metadata;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.Objects;

/**
 * @author CreeperFace
 */

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class BrewingStand implements Metadata {

    final boolean eastSlot;
    final boolean southwestSlot;
    final boolean northwestSlot;

    public static BrewingStand of(boolean eastSlot, boolean southwestSlot, boolean northwestSlot) {
        return new BrewingStand(eastSlot, southwestSlot, northwestSlot);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eastSlot, southwestSlot, northwestSlot);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BrewingStand that = (BrewingStand) o;
        return this.eastSlot == that.eastSlot && this.southwestSlot == that.southwestSlot && this.northwestSlot == that.northwestSlot;
    }

    @Override
    public String toString() {
        return "BrewingStand(" +
                "east slot=" + eastSlot +
                ", southwest slot=" + southwestSlot +
                ", northwest slot=" + northwestSlot +
                ')';
    }

    public boolean isSlotFilled(SlotType slot) {
        switch (slot) {
            case EAST:
                return eastSlot;
            case SOUTHWEST:
                return southwestSlot;
            case NORTHWEST:
                return northwestSlot;
        }

        return false;
    }

    public enum SlotType {
        EAST,
        SOUTHWEST,
        NORTHWEST
    }
}
