package cn.nukkit.api.metadata.block;

import cn.nukkit.api.metadata.Metadata;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Sponge implements Metadata {
    private final boolean wet;

    public boolean isWet() {
        return wet;
    }

    public int hashCode() {
        return wet ? 1 : 0;
    }

    public String toString() {
        return "Sponge(" +
                "wet=" + wet +
                ')';
    }
}
