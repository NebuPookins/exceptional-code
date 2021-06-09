package net.nebupookins.exceptional.util.stream;

import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Analogous to {@link IntStream}, but handles {@link Exception}s in a type-safe manner.
 * <p>
 * This API design deviates from the JDK's design in that an {@link EIntStream} is a {@link EStream} whose item type is
 * {@link Integer}, whereas there is no "is-a" relationship between {@link IntStream} and {@link Stream}.
 *
 * @param <E> the type of exception that can be thrown.
 */
public interface EIntStream<E extends Throwable> extends EStream<Integer, E> {
    /**
     * Returns the sum of all the values in this stream.
     *
     * @see IntStream#sum()
     */
    public int sum() throws E;

    public static EIntStream<RuntimeException> from(IntStream intStream) {
        return new SecretExceptionIntStreamImpl<>(intStream.boxed());
    }
}

class SecretExceptionIntStreamImpl<E extends Throwable> extends SecretExceptionStreamImpl<Integer, E> implements EIntStream<E> {
    SecretExceptionIntStreamImpl(Stream<Integer> delegate) {
        super(delegate);
    }

    @Override
    public int sum() throws E {
        return unwrapFromSecretException(() ->
                this.delegate.mapToInt(Integer::intValue).sum());
    }
}
