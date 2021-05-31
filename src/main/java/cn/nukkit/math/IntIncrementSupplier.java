package cn.nukkit.math;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

import java.util.function.IntSupplier;
import java.util.stream.IntStream;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public final class IntIncrementSupplier implements IntSupplier {
    private int next;
    private final int increment;

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public IntIncrementSupplier(int first, int increment) {
        next = first;
        this.increment = increment;
    }

    @Override
    public int getAsInt() {
        int current = next;
        next = current + increment;
        return current;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public IntStream stream() {
        return IntStream.generate(this); 
    }
}
