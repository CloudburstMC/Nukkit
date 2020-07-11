package cn.nukkit.math;

import java.util.function.IntSupplier;
import java.util.stream.IntStream;

public final class IntIncrementSupplier implements IntSupplier {
    private int next;
    private final int increment;
    
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
    
    public IntStream stream() {
        return IntStream.generate(this); 
    }
}
