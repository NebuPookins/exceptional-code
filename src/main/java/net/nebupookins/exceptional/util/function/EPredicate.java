package net.nebupookins.exceptional.util.function;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * Analogous to {@link Predicate}, but handles {@link Exception}s in a type-safe manner.
 * <p>
 * Represents a function which accepts an input, and either returns a boolean or throws an {@link Exception}.
 * <p>
 * Note that one particular deviation from the JDK's API design is that an {@link EPredicate} is an {@link EFunction}
 * whose output is a {@link Boolean}, whereas in the JDK there is no "is-a" relationship between {@link Predicate} and
 * {@link java.util.function.Function}.
 *
 * @param <I> the type of the input.
 * @param <E> the type of the exception that can be thrown.
 */
@FunctionalInterface
public interface EPredicate<I, E extends Throwable> extends EFunction<I, Boolean, E> {
    /**
     * @see Predicate#test(Object)
     */
    public boolean test(I input) throws E;

    @Override
    public default Boolean apply(I input) throws E {
        return this.test(input);
    }

    /**
     * Returns an {@link EPredicate} that is equivalent to performing a short-circuiting <code>and</code> between this
     * {@link EPredicate} and the provided one.
     *
     * @see Predicate#and(Predicate)
     */
    public default EPredicate<I, E> and(EPredicate<? super I, ? extends E> other) {
        return (I input) -> this.test(input) && other.test(input);
    }

    /**
     * Returns an {@link EPredicate} whose output is the opposite of this {@link EPredicate}.
     *
     * @see Predicate#negate()
     */
    public default EPredicate<I, E> negate() {
        return (I input) -> !this.test(input);
    }

    /**
     * Returns an {@link EPredicate} that is equivalent to performing a short-circuiting <code>or</code> between this
     * {@link EPredicate} and the provided one.
     *
     * @see Predicate#or(Predicate)
     */
    public default EPredicate<I, E> or(EPredicate<? super I, ? extends E> other) {
        return (I input) -> this.test(input) || other.test(input);
    }

    /**
     * Returns an {@link EPredicate} that checks whether the value provided to it is equal to the value provided to this
     * method, using {@link java.util.Objects#equals(Object, Object)}.
     *
     * @see Predicate#isEqual(Object)
     */
    public static <I> EPredicate<I, RuntimeException> isEqual(Object targetRef) {
        return (I input) -> Objects.equals(targetRef, input);
    }

    static <I> EPredicate<I, RuntimeException> from(Predicate<I> predicate) {
        return predicate::test;
    }
}
