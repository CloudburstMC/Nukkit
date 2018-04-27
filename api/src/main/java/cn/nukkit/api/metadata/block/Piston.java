package cn.nukkit.api.metadata.block;

import cn.nukkit.api.util.data.BlockFace;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import java.util.Objects;

public class Piston extends Directional {
    private final boolean extended;

    private Piston(BlockFace face, boolean extended) {
        super(face);
        this.extended = extended;
    }

    public static Piston of(@Nonnull BlockFace face, boolean extended) {
        Preconditions.checkNotNull(face, "face");
        return new Piston(face, extended);
    }

    public boolean isExtended() {
        return extended;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFace(), extended);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Piston that = (Piston) o;
        return this.getFace() == that.getFace() && this.extended == that.extended;
    }

    @Override
    public String toString() {
        return "Piston(" +
                "direction=" + getFace() +
                ", extended=" + extended +
                ')';
    }
}
