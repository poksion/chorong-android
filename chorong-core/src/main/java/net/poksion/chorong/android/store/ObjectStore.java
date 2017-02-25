package net.poksion.chorong.android.store;

public interface ObjectStore {

    interface PersistenceProxy {
        void setData(String conditions, Object data);
        Object getData(String conditions);
    }

    final class Key {

        final String staticKey;
        final String conditions;

        public Key(String staticKey) {
            this(staticKey, "");
        }

        public Key(String staticKey, String conditions) {
            this.staticKey = staticKey;
            this.conditions = conditions;
        }

    }

    void set(Key key, Object value);
    Object get(Key key);

    void setPersistenceProxy(String staticKey, PersistenceProxy persistenceProxy);
    void addWeakObserver(String staticKey, StoreObserver observer, boolean readExistValue);

}
