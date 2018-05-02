package com.nukkitx.api.metadata.block;

import com.google.common.base.Preconditions;
import com.nukkitx.api.metadata.Metadata;
import com.nukkitx.api.util.data.BlockFace;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import javax.annotation.Nonnull;
import java.util.Objects;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class Directional implements Metadata {
    private final BlockFace face;

    public BlockFace getFace() {
        return face;
    }

    public static Directional of(@Nonnull BlockFace face) {
        Preconditions.checkNotNull(face, "face");
        return new Directional(face);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Directional that = (Directional) o;
        return face == that.face;
    }

    @Override
    public int hashCode() {
        return Objects.hash(face);
    }

    @Override
    public String toString() {
        return "Directional(" +
                "face=" + face +
                ')';
    }
}
