package net.poksion.chorong.android.store;

public class StoreAccessor<V> {

    private final ObjectStore.Key key;
    private final ObjectStore store;

    public StoreAccessor(String key, ObjectStore store) {
        this(new ObjectStore.Key(key), store);
    }

    public StoreAccessor(ObjectStore.Key key, ObjectStore store) {
        this.key = key;
        this.store = store;
    }

    public V read() {
        Object data = store.get(key);

        @SuppressWarnings("unchecked")
        V typed = (V)data;
        return typed;
    }

    public void write(V value) {
        store.set(key, value);
    }
}
