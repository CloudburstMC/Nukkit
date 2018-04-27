package cn.nukkit.api.metadata.block;

import cn.nukkit.api.metadata.data.SimpleDirection;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import java.util.Objects;

public class Pumpkin extends SimpleDirectional {
    private final boolean withoutFace;

    private Pumpkin(SimpleDirection direction, boolean withoutFace) {
        super(direction);
        this.withoutFace = withoutFace;
    }

    public static Pumpkin of(@Nonnull SimpleDirection direction, boolean withoutFace) {
        Preconditions.checkNotNull(direction, "direction");
        return new Pumpkin(direction, withoutFace);
    }

    public boolean isWithoutFace() {
        return withoutFace;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDirection(), withoutFace);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pumpkin that = (Pumpkin) o;
        return this.getDirection() == that.getDirection() && this.withoutFace == that.withoutFace;
    }

    @Override
    public String toString() {
        return "Pumpkin(" +
                "direction=" + getDirection() +
                ", isWithoutFace=" + withoutFace +
                ')';
    }
}
