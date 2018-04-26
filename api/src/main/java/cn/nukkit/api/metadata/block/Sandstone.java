package cn.nukkit.api.metadata.block;

import cn.nukkit.api.metadata.Metadata;
import cn.nukkit.api.metadata.data.SandstoneType;
import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import javax.annotation.Nonnull;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Sandstone implements Metadata {
    private final SandstoneType type;

    public static Sandstone of(@Nonnull SandstoneType type) {
        Preconditions.checkNotNull(type);
        return new Sandstone(type);
    }

    public SandstoneType getType() {
        return type;
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sandstone that = (Sandstone) o;
        return this.type == that.type;
    }

    @Override
    public String toString() {
        return "Sandstone(" +
                "type=" + type +
                ')';
    }
}
