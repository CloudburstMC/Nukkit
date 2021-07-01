package cn.nukkit.utils.functional;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.LongUnaryOperator;

/**
 * Represents a function that accepts three arguments where the last is long and produces a long result.
 * This is the three-arity specialization of {@link Function}.
 *
 * <p>This is a functional interface
 * whose functional method is {@link #apply(Object, Object, long)}.
 *
 * @param <F> the type of the first argument to the function
 * @param <S> the type of the second argument to the function
 *
 * @see Function
 * @since 1.4.0.0-PN
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
@FunctionalInterface
public interface ToLongTriFunctionOneLong<F, S> {

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
    long apply(F f, S s, long t);

    /**
     * Returns a composed function that first applies this function to
     * its input, and then applies the {@code after} function to the result.
     * If evaluation of either function throws an exception, it is relayed to
     * the caller of the composed function.
     *
     * @param after the function to apply after this function is applied
     * @return a composed function that first applies this function and then
     * applies the {@code after} function
     * @throws NullPointerException if after is null
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    default ToLongTriFunctionOneLong<F, S> andThen(LongUnaryOperator after) {
        Objects.requireNonNull(after);
        return (F f, S s, long t) -> after.applyAsLong(apply(f, s, t));
    }
}
