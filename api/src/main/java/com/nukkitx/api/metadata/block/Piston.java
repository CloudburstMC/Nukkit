package com.nukkitx.api.metadata.block;

import com.google.common.base.Preconditions;
import com.nukkitx.api.util.data.BlockFace;
import lombok.Getter;

import javax.annotation.Nonnull;
import java.util.Objects;

@Getter
public class Piston extends Directional {

    private final boolean extended;
    private final boolean sixSided;

    private Piston(BlockFace face, boolean extended, boolean sixSided) {
        super(face);
        this.extended = extended;
        this.sixSided = sixSided;
    }

    public static Piston of(@Nonnull BlockFace face, boolean extended) {
        Preconditions.checkNotNull(face, "face");
        return new Piston(face, extended, false);
    }

    public static Piston of(boolean extended) {
        return new Piston(BlockFace.BOTTOM, extended, true);
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
