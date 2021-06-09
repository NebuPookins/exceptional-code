package net.nebupookins.exceptional.util.function;

import java.util.function.BiConsumer;

/**
 * Analogous to {@link BiConsumer}, but handles {@link Exception}s in a type-safe manner.
 * <p>
 * Represents some code, presumably with side effects, that takes two parameters as input.
 */
public interface EBiConsumer<I1, I2, E extends Throwable> {

    /**
     * @see BiConsumer#accept(Object, Object)
     */
    public void accept(I1 t, I2 u) throws E;

    /**
     * Rreturns an {@link EBiConsumer} that is equivalent to executing this {@link EBiConsumer} and then executing the
     * provided {@link EBiConsumer}.
     *
     * @see BiConsumer#andThen(BiConsumer)
     */
    public default EBiConsumer<I1, I2, E> andThen(EBiConsumer<? super I1, ? super I2, ? extends E> after) {
        return (I1 input1, I2 input2) -> {
            this.accept(input1, input2);
            after.accept(input1, input2);
        };
    }

    public static <I1, I2> EBiConsumer<I1, I2, RuntimeException> from(BiConsumer<I1, I2> biconsumer) {
        return biconsumer::accept;
    }
}
