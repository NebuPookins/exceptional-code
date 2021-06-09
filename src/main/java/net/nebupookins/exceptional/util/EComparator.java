package net.nebupookins.exceptional.util;

import net.nebupookins.exceptional.util.function.EFunction;

import java.util.Comparator;
import java.util.function.Function;

/**
 * Analogous to {@link Comparator}, but handles {@link Exception}s in a type-safe manner.
 * <p>
 * Represents an ordering of the elements of type <code>I</code>. Whether this is a total ordering or a partial ordering
 * is "complicated". The javadocs for {@link Comparator} claim that it represents a total ordering, but this is
 * misleading: It is a total ordering only if we assume the {@link Comparator::compare} method never throws an
 * exception. Unfortunately, there's no way to communicate via the type system whether a particular instance of
 * {@link Comparator} intends to never throw exceptions or not.
 * <p>
 * In contrast, {@link EComparator} has a natural convention that can be used to indicate whether its
 * {@link EComparator#compare(Object, Object)} method intends to throw an exception (and thus represents a partial
 * ordering) or not (and thus represents a total ordering): by checking whether the generic type of the exception is
 * checked or unchecked. If an instance of {@link EComparator} declares that it might throw a checked exception, by
 * convention, this can be understood to indicate that the instance represents a partial ordering. If the
 * {@link EComparator} declares that it only throws unchecked exceptions (e.g. it declares that it throws
 * {@link RuntimeException}), by convention, this can be understood to indicate that the instance represents a total
 * ordering.
 * <p>
 * (In the case where an instance wishes to represent a partial ordering, but the natural exception it wishes to throw
 * is an unchecked exception, the author of the instance can wrap the unchecked exception in a checked exception in
 * order to follow the above-mentioned convention).
 *
 * @param <I> the type of objects which can be compared.
 * @param <E> the type of exception which may be thrown.
 */
public interface EComparator<I, E extends Throwable> {
    /**
     * @see Comparator#compare(Object, Object)
     */
    int compare(I input1, I input2) throws E;

    public static <I> EComparator<I, RuntimeException> from(Comparator<I> comparator) {
        return comparator::compare;
    }

    /**
     * @see Comparator#comparing(Function)
     */
    public static <I, O extends Comparable<? super O>, E extends Throwable> EComparator<I, E> comparing(
            EFunction<? super I, ? extends O, E> keyExtractor
    ) {
        return (I input1, I input2) -> {
            final O key1 = keyExtractor.apply(input1);
            final O key2 = keyExtractor.apply(input2);
            return key1.compareTo(key2);
        };
    }

    /**
     * @see Comparator#thenComparing(Comparator)
     */
    default EComparator<I, E> thenComparing(EComparator<? super I, ? extends E> other) {
        return (I input1, I input2) -> {
            int thisResult = this.compare(input1, input2);
            if (thisResult == 0) {
                return other.compare(input1, input2);
            } else {
                return thisResult;
            }
        };
    }
}
