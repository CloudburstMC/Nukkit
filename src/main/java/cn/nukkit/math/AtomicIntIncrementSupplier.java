package cn.nukkit.math;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntSupplier;
import java.util.stream.IntStream;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public final class AtomicIntIncrementSupplier implements IntSupplier {
    private final AtomicInteger next;
    private final int increment;

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public AtomicIntIncrementSupplier(int first, int increment) {
        next = new AtomicInteger(first);
        this.increment = increment;
    }

    @Override
    public int getAsInt() {
        return next.getAndAdd(increment);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public IntStream stream() {
        return IntStream.generate(this); 
    }
}
