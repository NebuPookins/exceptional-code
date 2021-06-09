package net.nebupookins.exceptional.util.function;

import java.util.function.Consumer;

/**
 * Analogous to {@link Consumer}, but handles {@link Exception}s in a type-safe manner.
 * <p>
 * Represents code that can accept some input and perform some "side effect" based on the input, possibly throwing an
 * {@link Exception}.
 *
 * @param <I> the type of the input.
 * @param <E> the type of the exception.
 */
@FunctionalInterface
public interface EConsumer<I, E extends Throwable> {
    /**
     * @see Consumer#accept(Object)
     */
    public void accept(I t) throws E;

    /**
     * Returns an {@link EConsumer} that is equivalent to executing this {@link EConsumer}, and then executing the
     * provided {@link EConsumer}.
     *
     * @see Consumer#andThen(Consumer)
     */
    public default EConsumer<I, E> andThen(EConsumer<? super I, ? extends E> after) {
        return (I input) -> {
            this.accept(input);
            after.accept(input);
        };
    }

    public static <I> EConsumer<I, RuntimeException> from(Consumer<I> consumer) {
        return consumer::accept;
    }
}
