package com.nukkitx.api.metadata.block;

import com.google.common.base.Preconditions;
import com.nukkitx.api.metadata.data.SimpleDirection;
import lombok.Getter;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * @author CreeperFace
 */
public class RedstoneRepeater extends SimpleDirectional {

    @Getter
    private final int delay;

    RedstoneRepeater(SimpleDirection direction, int delay) {
        super(direction);
        this.delay = delay;
    }

    public static RedstoneRepeater of(@Nonnull SimpleDirection direction, int delay) {
        Preconditions.checkNotNull(direction, "direction");
        return new RedstoneRepeater(direction, delay);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RedstoneRepeater)) return false;

        RedstoneRepeater that = (RedstoneRepeater) o;
        return delay == that.delay && that.getDirection() == getDirection();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), delay);
    }

    @Override
    public String toString() {
        return "RedstoneRepeater(" +
                "direction=" + getDirection() +
                ", delay=" + delay +
                ')';
    }
}
