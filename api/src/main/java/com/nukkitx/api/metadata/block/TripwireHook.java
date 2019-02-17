package com.nukkitx.api.metadata.block;

import com.google.common.base.Preconditions;
import com.nukkitx.api.metadata.data.SimpleDirection;
import lombok.Getter;

import java.util.Objects;

/**
 * @author CreeperFace
 */
public class TripwireHook extends SimpleDirectional {

    @Getter
    private final HookPosition position;

    TripwireHook(SimpleDirection direction, HookPosition pos) {
        super(direction);
        this.position = pos;
    }

    public static TripwireHook of(SimpleDirection direction, HookPosition pos) {
        Preconditions.checkNotNull(direction, "direction");
        return new TripwireHook(direction, pos);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TripwireHook)) return false;
        if (!super.equals(o)) return false;
        TripwireHook that = (TripwireHook) o;
        return position == that.position && getDirection() == that.getDirection();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), position);
    }

    @Override
    public String toString() {
        return "TripwireHook(" +
                "direction=" + getDirection() +
                ", blockPosition=" + position +
                ')';
    }

    public enum HookPosition {
        UP,
        MIDDLE,
        DOWN
    }
}
