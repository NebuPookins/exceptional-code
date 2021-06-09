package net.nebupookins.exceptional.sample.fakeclassesforexamples;

import net.nebupookins.exceptional.util.function.ESupplier;

import java.awt.*;

public class Widget {
    private final ESupplier<Integer, WidgetException> weight;
    private final ESupplier<Color, WidgetException> color;

    public Widget(ESupplier<Integer, WidgetException> weight, ESupplier<Color, WidgetException> color) {
        this.weight = weight;
        this.color = color;
    }

    public static Widget of(int weight, Color color) {
        return new Widget(() -> weight, () -> color);
    }

    public static Widget of(ESupplier<Integer, WidgetException> weight, ESupplier<Color, WidgetException> color) {
        return new Widget(weight, color);
    }

    @SuppressWarnings("RedundantThrows")
    public int getWeight() throws WidgetException {
        return weight.get();
    }

    @SuppressWarnings("RedundantThrows")
    public Color getColor() throws WidgetException {
        return color.get();
    }
}
