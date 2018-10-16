package com.nukkitx.api.metadata.block;

import com.google.common.base.Preconditions;
import com.nukkitx.api.metadata.Metadata;
import com.nukkitx.api.metadata.data.ActivableRailDirection;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * @author CreeperFace
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class ActivableRail implements Metadata {

    private final ActivableRailDirection direction;
    private final boolean active;

    public static ActivableRail of(@Nonnull ActivableRailDirection direction, boolean active) {
        Preconditions.checkNotNull(direction, "direction");
        return new ActivableRail(direction, active);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ActivableRail)) return false;
        ActivableRail that = (ActivableRail) o;
        return active == that.active &&
                direction == that.direction;
    }

    @Override
    public int hashCode() {

        return Objects.hash(direction, active);
    }

    @Override
    public String toString() {
        return "ActivableRail(" +
                "direction=" + direction +
                ", active=" + active +
                ')';
    }
}
