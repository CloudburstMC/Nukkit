package cn.nukkit.utils.function;

/**
 * A function that accepts an int as a primitive type, and returns an instance of <T>
 * @param <T> the type of object that will be returned
 *
 * @author DaPorkchop_
 */
public interface IntObjectFunction<T> {
    T accept(int i);
}
