package net.nebupookins.exceptional.sample.fakeclassesforexamples;

import java.util.Objects;

public class Item {
    private final String id;

    public Item(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item)) return false;
        Item item = (Item) o;
        return Objects.equals(id, item.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
