package net.poksion.chorong.android.store;

public class StoreAccessor<T_Value> {

    private final ObjectStore.Key key;
    private final ObjectStore store;

    public StoreAccessor(String key, ObjectStore store) {
        this(new ObjectStore.Key(key), store);
    }

    public StoreAccessor(ObjectStore.Key key, ObjectStore store) {
        this.key = key;
        this.store = store;
    }

    public T_Value read() {
        Object data = store.get(key);

        @SuppressWarnings("unchecked")
        T_Value typed = (T_Value)data;
        return typed;
    }

    public void write(T_Value value) {
        store.set(key, value);
    }
}
