package com.nukkitx.api.metadata.block;

import com.google.common.base.Objects;
import com.nukkitx.api.metadata.data.SimpleDirection;
import lombok.Getter;

/**
 * @author CreeperFace
 */
public class Bed extends SimpleDirectional {

    @Getter
    final boolean occupied;
    @Getter
    final Part part;

    Bed(SimpleDirection direction, boolean occupied, Part part) {
        super(direction);
        this.occupied = occupied;
        this.part = part;
    }

    public static Bed of(SimpleDirection direction, boolean occupied, Part part) {
        return new Bed(direction, occupied, part);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getDirection(), occupied, part);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bed that = (Bed) o;
        return this.getDirection() == that.getDirection() && this.occupied == that.occupied && this.part == that.part;
    }

    @Override
    public String toString() {
        return "Bed(" +
                "direction=" + getDirection() +
                "occupied=" + occupied +
                "part=" + part.name() +
                ')';
    }

    public enum Part {
        FOOT,
        HEAD
    }
}
