package cn.nukkit.api.metadata;

import cn.nukkit.api.metadata.data.DyeColor;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import java.util.Objects;

public class Dyed implements Metadata {
    public static final Dyed DEFAULT_DYE = Dyed.of(DyeColor.WHITE);
    private final DyeColor color;

    private Dyed(DyeColor color) {
        this.color = color;
    }

    @Nonnull
    public static Dyed of(@Nonnull DyeColor color) {
        Preconditions.checkNotNull(color, "color");
        return new Dyed(color);
    }

    @Nonnull
    public final DyeColor getColor() {
        return color;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dyed dyed = (Dyed) o;
        return color == dyed.color;
    }

    @Override
    public final int hashCode() {
        return Objects.hash(color);
    }

    @Override
    public final String toString() {
        return "Dyed{" +
                "color=" + color +
                '}';
    }
}
