package net.poksion.chorong.android.store;

import android.app.Application;

public class ObjectStoreApplication extends Application implements ObjectStore {

    private final ObjectStoreImpl objectStore = new ObjectStoreImpl();

    @Override
    public void set(Key key, Object value) {
        objectStore.set(key, value);
    }

    @Override
    public Object get(Key key) {
        return objectStore.get(key);
    }

    @Override
    public void setPersistenceProxy(String staticKey, PersistenceProxy persistenceProxy) {
        objectStore.setPersistenceProxy(staticKey, persistenceProxy);
    }

    @Override
    public void addWeakObserver(String staticKey, StoreObserver observer, boolean readExistValue) {
        objectStore.addWeakObserver(staticKey, observer, readExistValue);
    }
}
