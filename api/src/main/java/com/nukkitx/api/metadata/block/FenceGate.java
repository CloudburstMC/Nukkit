package com.nukkitx.api.metadata.block;

import com.google.common.base.Preconditions;
import com.nukkitx.api.metadata.data.SimpleDirection;
import lombok.Getter;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * @author CreeperFace
 */
public class FenceGate extends SimpleDirectional {

    @Getter
    private final boolean open;

    FenceGate(SimpleDirection direction, boolean open) {
        super(direction);
        this.open = open;
    }

    public static FenceGate of(@Nonnull SimpleDirection direction, boolean open) {
        Preconditions.checkNotNull(direction, "direction");
        return new FenceGate(direction, open);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FenceGate)) return false;

        FenceGate that = (FenceGate) o;
        return open == that.open && that.getDirection() == getDirection();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), open);
    }

    @Override
    public String toString() {
        return "Trapdoor(" +
                "direction=" + getDirection() +
                ", open=" + open +
                ')';
    }
}
