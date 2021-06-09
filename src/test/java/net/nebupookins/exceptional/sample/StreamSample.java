package net.nebupookins.exceptional.sample;

import net.nebupookins.exceptional.sample.fakeclassesforexamples.*;
import net.nebupookins.exceptional.util.function.EFunction;
import net.nebupookins.exceptional.util.stream.EStream;
import org.easymock.EasyMockSupport;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.awt.Color.BLUE;
import static java.awt.Color.RED;
import static net.nebupookins.exceptional.hamcrest.ThrowsExceptionMatcher.throwsException;
import static org.easymock.EasyMock.expect;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class StreamSample extends EasyMockSupport {

    /**
     * An example of a simple usage of {@link EStream#map(EFunction)}.
     */
    public static List<Item> simpleMapExample(final DataStore dataStore, final List<String> itemIds) throws DBConnectionException {
        return EStream.<String, DBConnectionException>from(itemIds)
                .map(id -> dataStore.fetchItem(id))
                .collect(Collectors.toList());
    }

    /**
     * Shows the JDK Javadoc Widget example at https://docs.oracle.com/javase/8/docs/api/java/util/stream/Stream.html
     * as it would appear using the exceptional-code API instead.
     */
    public static int jdkJavadocWidgetExample(List<Widget> widgets) throws WidgetException {
        int sum = EStream.<Widget, WidgetException>from(widgets)
                .filter(w -> w.getColor() == RED)
                .mapToInt(w -> w.getWeight())
                .sum();
        return sum;
    }

    @Test
    public void simpleMapExample_whenDataStoreDoesntThrowException() throws DBConnectionException {
        final List<String> itemIds = Arrays.asList("1", "2", "3");

        final DataStore dataStore = createMock(DataStore.class);
        for (final String id : itemIds) {
            expect(dataStore.fetchItem(id)).andReturn(new Item(id)).once();
        }
        replayAll();

        List<Item> result = simpleMapExample(dataStore, itemIds);
        assertThat(result, is(Arrays.asList(new Item("1"), new Item("2"), new Item("3"))));
        verifyAll();
    }

    @Test
    public void simpleMapExample_whenDataStoreDoesThrowException() throws DBConnectionException {
        final List<String> itemIds = Arrays.asList("1", "2", "3");
        final DBConnectionException fakeExceptionForTest = new DBConnectionException();

        final DataStore dataStore = createMock(DataStore.class);
        expect(dataStore.fetchItem("1")).andReturn(new Item("1")).once();
        expect(dataStore.fetchItem("2")).andThrow(fakeExceptionForTest);
        replayAll();

        assertThat(() -> simpleMapExample(dataStore, itemIds), throwsException(is(fakeExceptionForTest)));
        verifyAll();
    }

    @Test
    public void jdkJavadocWidgetExample_whenNothingThrowsAnException() throws WidgetException {
        final List<Widget> widgets = Arrays.asList(
                Widget.of(1, RED), Widget.of(2, BLUE), Widget.of(3, RED));
        int expectedSum = 4; //1 + 3 are red, 2 is blue.
        int actualSum = jdkJavadocWidgetExample(widgets);
        assertThat(actualSum, is(expectedSum));
    }

    @Test
    public void jdkJavadocWidgetExample_whenGetColorThrowsAnException() {
        final WidgetException fakeExceptionForTest = new WidgetException();
        final List<Widget> widgets = Arrays.asList(
                Widget.of(1, RED),
                Widget.of(() -> 2, () -> {
                    throw fakeExceptionForTest;
                }),
                Widget.of(3, RED));
        assertThat(() -> jdkJavadocWidgetExample(widgets), throwsException(is(fakeExceptionForTest)));
    }

    @Test
    public void jdkJavadocWidgetExample_whenGetWeightThrowsAnExceptionButItGetsFilteredOutForBeingBlue() throws WidgetException {
        final WidgetException fakeExceptionForTest = new WidgetException();
        final List<Widget> widgets = Arrays.asList(
                Widget.of(1, RED),
                Widget.of(() -> {
                    throw fakeExceptionForTest;
                }, () -> BLUE),
                Widget.of(3, RED));
        int expectedSum = 4; //1 + 3 are red, 2 is blue.
        int actualSum = jdkJavadocWidgetExample(widgets);
        assertThat(actualSum, is(expectedSum));
    }

    @Test
    public void jdkJavadocWidgetExample_whenGetWeightThrowsAnExceptionAndIsntFilteredOut() {
        final WidgetException fakeExceptionForTest = new WidgetException();
        final List<Widget> widgets = Arrays.asList(
                Widget.of(1, RED),
                Widget.of(2, BLUE),
                Widget.of(() -> {
                    throw fakeExceptionForTest;
                }, () -> RED));
        assertThat(() -> jdkJavadocWidgetExample(widgets), throwsException(is(fakeExceptionForTest)));
    }
}
