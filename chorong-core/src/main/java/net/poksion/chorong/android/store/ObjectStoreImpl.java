package net.poksion.chorong.android.store;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.poksion.chorong.android.store.internal.Subject;

public class ObjectStoreImpl implements ObjectStore {

    private final Subject observableContainer = new Subject();
    private final Map<String, PersistenceProxy> persistenceProxies = new ConcurrentHashMap<>();

    @Override
    public void set(Key key, Object object) {
        PersistenceProxy persistenceProxy = persistenceProxies.get(key.staticKey);

        if (persistenceProxy != null) {
            persistenceProxy.setData(key.conditions, object);
            observableContainer.set(key.staticKey, key.conditions);
        } else {
            observableContainer.set(key.staticKey, object);
        }
    }


    @Override
    public Object get(Key key) {
        PersistenceProxy persistenceProxy = persistenceProxies.get(key.staticKey);

        if (persistenceProxy != null) {
            return persistenceProxy.getData(key.conditions);
        }

        return observableContainer.get(key.staticKey);
    }

    @Override
    public void setPersistenceProxy(String staticKey, PersistenceProxy persistenceProxy) {
        persistenceProxies.put(staticKey, persistenceProxy);
    }

    @Override
    public void addWeakObserver(String staticKey, StoreObserver observer, boolean readExistValue) {
        final PersistenceProxy persistenceProxy = persistenceProxies.get(staticKey);
        if(persistenceProxy != null) {
            observer.objectProxy = new StoreObserver.ObjectProxy() {
                @Override
                Object getObject(String conditions) {
                    return persistenceProxy.getData(conditions);
                }
            };
        }

        observableContainer.addWeakObserver(staticKey, observer, readExistValue);

        // for first observing on persistence data
        if(readExistValue && persistenceProxy != null && observableContainer.get(staticKey) == null) {
            observableContainer.set(staticKey, "");
        }
    }
}
