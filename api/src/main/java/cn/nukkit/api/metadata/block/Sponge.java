package cn.nukkit.api.metadata.block;

import cn.nukkit.api.metadata.Metadata;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Sponge implements Metadata {
    private final boolean wet;

    public boolean isWet() {
        return wet;
    }

    @Override
    public int hashCode() {
        return wet ? 1 : 0;
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
