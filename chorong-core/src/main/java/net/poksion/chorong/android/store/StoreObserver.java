package net.poksion.chorong.android.store;

import net.poksion.chorong.android.store.internal.Observer;

public abstract class StoreObserver<V> implements Observer {

    static abstract class ObjectProxy {
        abstract Object getObject(String conditions);
    }
    ObjectProxy objectProxy = null;

    protected abstract void onChanged(V value);

    @SuppressWarnings("unchecked")
    @Override
    public final void onObjectChanged(Object object) {
        V value = null;

        try {
            if (objectProxy != null) {
                value = (V) objectProxy.getObject((String)object);
            } else {
                value = (V) object;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (value != null) {
            onChanged(value);
        }
    }
}
