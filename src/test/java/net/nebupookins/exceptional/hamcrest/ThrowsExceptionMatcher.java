package net.nebupookins.exceptional.hamcrest;

import net.nebupookins.exceptional.lang.ERunnable;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

public class ThrowsExceptionMatcher<E extends Throwable> extends TypeSafeDiagnosingMatcher<ERunnable<E>> {
    private final Matcher<? super E> throwableMatcher;

    public ThrowsExceptionMatcher(final Matcher<? super E> throwableMatcher) {
        this.throwableMatcher = throwableMatcher;
    }

    public static <E extends Throwable> ThrowsExceptionMatcher<E> throwsException(Matcher<? super E> exceptionMatcher) {
        return new ThrowsExceptionMatcher<E>(exceptionMatcher);
    }

    public static <E extends Throwable> ThrowsExceptionMatcher<E> throwsException() {
        return throwsException(CoreMatchers.notNullValue());
    }

    @Override
    protected boolean matchesSafely(ERunnable<E> runnable, Description description) {
        try {
            runnable.run();
            description.appendText("no exception thrown");
            return false;
        } catch (Throwable e) {
            boolean retVal = throwableMatcher.matches(e);
            if (!retVal) {
                description.appendText("exception thrown is ");
                description.appendValue(e);
            }
            return retVal;
        }
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("exception thrown ");
        description.appendDescriptionOf(throwableMatcher);
    }
}
