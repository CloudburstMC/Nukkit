package cn.nukkit.api.metadata.block;

import cn.nukkit.api.metadata.Metadata;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Sponge implements Metadata {
    private final boolean wet;

    public static Sponge of(boolean isWet) {
        return new Sponge(isWet);
    }

    public boolean isWet() {
        return wet;
    }

    @Override
    public int hashCode() {
        return Boolean.hashCode(wet);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sponge that = (Sponge) o;
        return this.wet == that.wet;
    }

    @Override
    public String toString() {
        return "Sponge(" +
                "wet=" + wet +
                ')';
    }
}
