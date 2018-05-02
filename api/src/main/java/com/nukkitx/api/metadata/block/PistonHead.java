package com.nukkitx.api.metadata.block;

import com.google.common.base.Preconditions;
import com.nukkitx.api.util.data.BlockFace;

import javax.annotation.Nonnull;
import java.util.Objects;

public class PistonHead extends Directional {
    private final boolean sticky;

    private PistonHead(BlockFace face, boolean sticky) {
        super(face);
        this.sticky = sticky;
    }

    public static PistonHead of(@Nonnull BlockFace face, boolean sticky) {
        Preconditions.checkNotNull(face, "face");
        return new PistonHead(face, sticky);
    }

    public boolean isSticky() {
        return sticky;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFace(), sticky);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PistonHead that = (PistonHead) o;
        return this.getFace() == that.getFace() && this.sticky == that.sticky;
    }
}
