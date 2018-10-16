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
public class Trapdoor extends SimpleDirectional {

    private final boolean topHalf;
    private final boolean open;

    Trapdoor(SimpleDirection direction, boolean open, boolean topHalf) {
        super(direction);
        this.topHalf = topHalf;
        this.open = open;
    }

    public static Trapdoor of(@Nonnull SimpleDirection direction, boolean open, boolean topHalf) {
        Preconditions.checkNotNull(direction, "direction");
        return new Trapdoor(direction, open, topHalf);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Trapdoor)) return false;

        Trapdoor that = (Trapdoor) o;
        return topHalf == that.topHalf && open == that.open && that.getDirection() == getDirection();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), topHalf, open);
    }

    @Override
    public String toString() {
        return "Trapdoor(" +
                "topHalf=" + topHalf +
                ", direction=" + getDirection() +
                ", open=" + open +
                ')';
    }
}
