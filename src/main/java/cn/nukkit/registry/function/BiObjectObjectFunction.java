package cn.nukkit.registry.function;

/**
 * A function that accepts two parameters (as objects) and returns a new object
 *
 * @author DaPorkchop_
 */
public interface BiObjectObjectFunction<In1, In2, T> extends BaseFunction<T> {
    T accept(In1 a, In2 b);
}
