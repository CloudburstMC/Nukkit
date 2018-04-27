package cn.nukkit.api.metadata.block;

import cn.nukkit.api.metadata.data.SimpleDirection;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import java.util.Objects;

public class Stairs extends SimpleDirectional {
    private final boolean upsideDown;

    private Stairs(SimpleDirection direction, boolean upsideDown) {
        super(direction);
        this.upsideDown = upsideDown;
    }

    public static Stairs of(@Nonnull SimpleDirection direction, boolean upsideDown) {
        Preconditions.checkNotNull(direction, "direction");
        return new Stairs(direction, upsideDown);
    }

    public boolean isUpsideDown() {
        return upsideDown;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDirection(), upsideDown);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stairs that = (Stairs) o;
        return this.getDirection() == that.getDirection() && this.upsideDown == that.upsideDown;
    }

    @Override
    public String toString() {
        return "Stairs(" +
                "direction=" + getDirection() +
                ", isUpsideDown=" + upsideDown +
                ')';
    }
}
