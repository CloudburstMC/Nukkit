package cn.nukkit.utils.functional;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

import java.util.Objects;
import java.util.function.Function;

/**
 * Represents a function that accepts three arguments and produces a result.
 * This is the three-arity specialization of {@link Function}.
 *
 * <p>This is a functional interface
 * whose functional method is {@link #apply(Object, Object, Object)}.
 *
 * @param <F> the type of the first argument to the function
 * @param <S> the type of the second argument to the function
 * @param <T> the type of the third argument to the function
 * @param <R> the type of the result of the function
 *
 * @see Function
 * @since 1.4.0.0-PN
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
@FunctionalInterface
public interface TriFunction<F, S, T, R> {

    /**
     * Applies this function to the given arguments.
     *
     * @param f the first function argument
     * @param s the second function argument
     * @param t the third function argument
     * @return the function result
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    R apply(F f, S s, T t);

    /**
     * Returns a composed function that first applies this function to
     * its input, and then applies the {@code after} function to the result.
     * If evaluation of either function throws an exception, it is relayed to
     * the caller of the composed function.
     *
     * @param <V> the type of output of the {@code after} function, and of the
     *           composed function
     * @param after the function to apply after this function is applied
     * @return a composed function that first applies this function and then
     * applies the {@code after} function
     * @throws NullPointerException if after is null
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    default <V> TriFunction<F, S, T, V> andThen(Function<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (F f, S s, T t) -> after.apply(apply(f, s, t));
    }
}
