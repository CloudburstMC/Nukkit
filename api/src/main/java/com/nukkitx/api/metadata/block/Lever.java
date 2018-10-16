package com.nukkitx.api.metadata.block;

import com.google.common.base.Preconditions;
import com.nukkitx.api.metadata.Powerable;
import com.nukkitx.api.metadata.data.SimpleDirection;
import lombok.Getter;

@Getter
public class Lever extends SimpleDirectional implements Powerable {

    private final boolean powered;
    private final LeverPos position;

    private Lever(SimpleDirection direction, LeverPos position, boolean powered) {
        super(direction);
        this.powered = powered;
        this.position = position;
    }

    public static Lever of(SimpleDirection directional, LeverPos position, boolean powered) {
        Preconditions.checkArgument(directional != null, "SimpleDirection cannot be null");
        return new Lever(directional, position, powered);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lever that = (Lever) o;
        return this.powered == that.powered && this.getDirection() == that.getDirection() && this.position == that.position;
    }

    @Override
    public int hashCode() {
        return 31 * (powered ? 1 : 0);
    }

    @Override
    public String toString() {
        return "Lever(" +
                "powered=" + powered +
                ", direction=" + getDirection() +
                ", position=" + position +
                ')';
    }

    public enum LeverPos {
        SIDE,
        TOP,
        BOTTOM
    }
}
