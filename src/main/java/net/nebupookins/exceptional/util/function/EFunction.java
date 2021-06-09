package net.nebupookins.exceptional.util.function;

import java.util.function.Function;

/**
 * Analogous to {@link Function}, but handles {@link Exception}s in a type-safe manner.
 * <p>
 * Represents a function that accepts an input, and either produces an output or throws an {@link Exception}.
 *
 * @param <I> the type of the Input.
 * @param <O> the type of the Output
 * @param <E> the type of the Exception thrown
 */
@FunctionalInterface
public interface EFunction<I, O, E extends Throwable> {
    /**
     * @see Function#apply(Object)
     */
    public O apply(I input) throws E;

    /**
     * Returns an {@link EFunction} that is equivalent to executing this {@link EFunction}, and then executing the
     * provided {@link EFunction}.
     *
     * @see Function#andThen(Function)
     */
    public default <O2> EFunction<I, O2, E> andThen(EFunction<? super O, ? extends O2, ? extends E> after) {
        return (I input) -> {
            final O intermediateValue = this.apply(input);
            return after.apply(intermediateValue);
        };
    }

    /**
     * Returns an {@link EFunction} that is equivalent to executing the provided {@link EFunction}, and then executing
     * this {@link EFunction}.
     *
     * @see Function#compose(Function)
     */
    public default <I2> EFunction<I2, O, E> compose(EFunction<? super I2, ? extends I, ? extends E> before) {
        return (I2 input) -> {
            final I intermediateValue = before.apply(input);
            return this.apply(intermediateValue);
        };
    }

    /**
     * Returns an {@link EFunction} that always the value that was passed in.
     *
     * @see Function#identity()
     */
    public static <T> EFunction<T, T, RuntimeException> identity() {
        return (T input) -> input;
    }

    /**
     * Returns an {@link EFunction} that is equivalent to the provided {@link Function}. The returned {@link EFunction}
     * will declare that it throws {@link RuntimeException}.
     *
     * @param f   the {@link Function} to convert into an {@link EFunction}.
     * @param <I> the type of the input.
     * @param <O> the type of the output.
     * @return an {@link EFunction} that is equivalent to the provided {@link Function}.
     */
    public static <I, O> EFunction<I, O, RuntimeException> from(Function<I, O> f) {
        return f::apply;
    }
}
