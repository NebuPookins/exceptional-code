package net.nebupookins.exceptional.util.function;

import java.util.function.Supplier;

/**
 * Analogous to {@link Supplier}, but handles {@link Exception}s in a type-safe manner.
 * <p>
 * Represents a piece of code that supplies a value (or throws an {@link Exception}).
 *
 * @param <O> the type of the value supplied.
 * @param <E> the type of the exception thrown.
 */
@FunctionalInterface
public interface ESupplier<O, E extends Throwable> {
    /**
     * @see Supplier#get()
     */
    public O get() throws E;

    public static <O> ESupplier<O, RuntimeException> from(Supplier<O> supplier) {
        return supplier::get;
    }
}
