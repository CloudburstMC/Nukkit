package cn.nukkit.registry.function;

/**
 * @author DaPorkchop_
 */
public interface IntIntegerObjectFunction<T> extends BaseFunction<T> {
    T accept(int a, Integer b);
}
