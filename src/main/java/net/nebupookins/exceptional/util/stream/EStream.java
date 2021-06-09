package net.nebupookins.exceptional.util.stream;

import net.nebupookins.exceptional.util.function.EFunction;
import net.nebupookins.exceptional.util.function.EPredicate;
import net.nebupookins.exceptional.util.function.ESupplier;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.Collector;
import java.util.stream.Stream;

/**
 * Analogous to {@link Stream}, but handles {@link Exception}s in a type-safe manner.
 *
 * @param <T> the type of items contained in the stream.
 * @param <E> the type of exception that may be thrown when a terminal method is called.
 */
public interface EStream<T, E extends Throwable> {

    public static <I, E extends Throwable> EStream<I, E> from(Stream<I> stream) {
        return new SecretExceptionStreamImpl<I, E>(stream);
    }

    public static <I, E extends Throwable> EStream<I, E> from(Collection<I> stream) {
        return new SecretExceptionStreamImpl<I, E>(stream.stream());
    }

    /**
     * Returns an {@link EStream} whose items are the items in this {@link EStream} for which the provided
     * {@link EPredicate} return true.
     *
     * @see Stream#filter(Predicate)
     */
    public EStream<T, E> filter(EPredicate<? super T, E> predicate);

    /**
     * Returns an {@link EStream} whose items are the result of applying the provided mapping {@link EFunction} to each item in
     * this {@link EStream}.
     *
     * @see Stream#map(Function)
     */
    public <I2> EStream<I2, E> map(EFunction<? super T, ? extends I2, ? extends E> mapper);

    /**
     * Returns an {@link EIntStream} whose items are the result of applying the provided mapping {@link EFunction} to
     * each item in this {@link EStream}.
     *
     * @see Stream#mapToInt(ToIntFunction)
     */
    public EIntStream<E> mapToInt(EFunction<? super T, ? extends Integer, ? extends E> mapper);

    /**
     * @see Stream#collect(Collector)
     */
    public <O, A> O collect(Collector<? super T, A, O> collector) throws E;
}

class SecretExceptionStreamImpl<T, E extends Throwable> implements EStream<T, E> {
    protected static class SecretException extends RuntimeException {
        private SecretException(Throwable cause) {
            super(null, cause, false, false);
        }
    }

    protected final Stream<T> delegate;

    public SecretExceptionStreamImpl(Stream<T> delegate) {
        this.delegate = delegate;
    }

    private static <O> O wrapInSecretException(ESupplier<O, Throwable> supplier) {
        try {
            return supplier.get();
        } catch (final RuntimeException | Error e) {
            throw e;
        } catch (final Throwable e) {
            throw new SecretException(e);
        }
    }

    protected <O> O unwrapFromSecretException(Supplier<O> supplier) throws E {
        try {
            return supplier.get();
        } catch (SecretException e) {
            throw (E) e.getCause();
        }
    }

    @Override
    public EStream<T, E> filter(EPredicate<? super T, E> predicate) {
        return new SecretExceptionStreamImpl<T, E>(
                this.delegate.filter((T input) ->
                        wrapInSecretException(() ->
                                predicate.test(input))));
    }

    @Override
    public <I2> EStream<I2, E> map(EFunction<? super T, ? extends I2, ? extends E> mapper) {
        return new SecretExceptionStreamImpl<I2, E>(
                this.delegate.<I2>map((T input) ->
                        wrapInSecretException(() ->
                                mapper.apply(input)))
        );
    }

    @Override
    public EIntStream<E> mapToInt(EFunction<? super T, ? extends Integer, ? extends E> mapper) {
        return new SecretExceptionIntStreamImpl<E>(this.delegate
                .map((T input) ->
                        wrapInSecretException(() ->
                                mapper.apply(input))));
    }

    @Override
    public <O, A> O collect(Collector<? super T, A, O> collector) throws E {
        return unwrapFromSecretException(() ->
                delegate.collect(collector));
    }
}
