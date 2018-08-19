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

    @Getter
    private final boolean powered;

    @Getter
    private final boolean rightHinge;

    @Getter
    private final boolean upperPart;

    Door(boolean rightHinge, boolean powered) {
        super(SimpleDirection.NORTH);
        this.open = false;
        this.powered = powered;
        this.upperPart = true;
        this.rightHinge = rightHinge;
    }

    Door(SimpleDirection direction, boolean open) {
        super(direction);
        this.open = open;
        this.upperPart = false;

        this.powered = false;
        this.rightHinge = false;
    }

    public static Door of(@Nonnull SimpleDirection direction, boolean open) {
        Preconditions.checkNotNull(direction, "direction");
        return new Door(direction, open);
    }

    public static Door of(boolean rightHinge, boolean powered) {
        return new Door(rightHinge, powered);
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

        return this.getDirection() == that.getDirection() && this.open == that.open && this.powered == that.powered && this.rightHinge == that.rightHinge && this.upperPart == that.upperPart;
    }

    @Override
    public String toString() {
        return "Door(" +
                "direction=" + getDirection() +
                ", isOpen=" + open +
                ", rightHinge=" + rightHinge +
                ", isPowered=" + powered +
                ", isUpperPart=" + upperPart +
                ')';
    }
}
