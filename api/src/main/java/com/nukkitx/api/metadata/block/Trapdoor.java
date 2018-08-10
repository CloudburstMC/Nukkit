package com.nukkitx.api.metadata.block;

import com.nukkitx.api.metadata.data.SimpleDirection;

import java.util.Objects;

/**
 * @author CreeperFace
 */
public class Trapdoor extends Door {

    private final boolean topHalf;

    public Trapdoor(SimpleDirection direction, boolean open, boolean topHalf) {
        super(direction, open);
        this.topHalf = topHalf;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDirection(), isOpen(), topHalf);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trapdoor that = (Trapdoor) o;
        return this.getDirection() == that.getDirection() && this.isOpen() == that.isOpen() && this.topHalf == that.topHalf;
    }

    @Override
    public String toString() {
        return "Trapdoor(" +
                "direction=" + getDirection() +
                ", isOpen=" + isOpen() +
                ", isOnTopHalf=" + topHalf +
                ')';
    }
}
