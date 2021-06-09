package net.nebupookins.exceptional.sample.fakeclassesforexamples;

public class DataStore {
    public Item fetchItem(String id) throws DBConnectionException {
        return new Item(id);
    }
}
