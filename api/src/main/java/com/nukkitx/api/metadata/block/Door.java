package com.nukkitx.api.metadata.block;

import com.google.common.base.Preconditions;
import com.nukkitx.api.metadata.data.SimpleDirection;
import lombok.Getter;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * @author CreeperFace
 */
public class Door extends SimpleDirectional {

    @Getter
    private final boolean open;

    Door(SimpleDirection direction, boolean open) {
        super(direction);
        this.open = open;
    }

    public static Door of(@Nonnull SimpleDirection direction, boolean open) {
        Preconditions.checkNotNull(direction, "direction");
        return new Door(direction, open);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDirection(), open);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Door that = (Door) o;
        return this.getDirection() == that.getDirection() && this.open == that.open;
    }

    @Override
    public String toString() {
        return "Door(" +
                "direction=" + getDirection() +
                ", isOpen=" + open +
                ')';
    }
}
