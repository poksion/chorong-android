package net.poksion.chorong.android.store;

import net.poksion.chorong.android.store.internal.Observer;

public abstract class StoreObserver<T_Value> implements Observer {

    static abstract class ObjectProxy {
        abstract Object getObject(String conditions);
    }
    ObjectProxy objectProxy = null;

    protected abstract void onChanged(T_Value value);

    @SuppressWarnings("unchecked")
    @Override
    public final void onObjectChanged(Object object) {
        T_Value value = null;

        try {
            if (objectProxy != null) {
                value = (T_Value) objectProxy.getObject((String)object);
            } else {
                value = (T_Value) object;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (value != null) {
            onChanged(value);
        }
    }
}
