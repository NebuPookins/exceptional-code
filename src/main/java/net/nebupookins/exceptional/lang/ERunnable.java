package net.nebupookins.exceptional.lang;

/**
 * Analogous to {@link Runnable}, but handles {@link Exception}s in a type-safe manner.
 * <p>
 * Represents a piece of code (usually with some side effect) that can be executed.
 *
 * @param <E> the type of exception thrown.
 */
@FunctionalInterface
public interface ERunnable<E extends Throwable> {
    /**
     * Executes the code represented by this runnable.
     */
    void run() throws E;
}
