package com.nukkitx.api.metadata.block;

import com.google.common.base.Preconditions;
import com.nukkitx.api.metadata.data.SimpleDirection;
import lombok.Getter;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * @author CreeperFace
 */
@Getter
public class RedstoneComparator extends SimpleDirectional {

    private final boolean subtractionMode;
    private final boolean powered;

    RedstoneComparator(SimpleDirection direction, boolean subtractionMode, boolean powered) {
        super(direction);
        this.subtractionMode = subtractionMode;
        this.powered = powered;
    }

    public static RedstoneComparator of(@Nonnull SimpleDirection direction, boolean subtractionMode, boolean powered) {
        Preconditions.checkNotNull(direction, "direction");
        return new RedstoneComparator(direction, subtractionMode, powered);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RedstoneComparator)) return false;

        RedstoneComparator that = (RedstoneComparator) o;
        return subtractionMode == that.subtractionMode && powered == that.powered && that.getDirection() == getDirection();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), powered, subtractionMode);
    }

    @Override
    public String toString() {
        return "RedstoneComparator(" +
                "subtractionMode=" + subtractionMode +
                ", direction=" + getDirection() +
                ", powered=" + powered +
                ')';
    }
}
