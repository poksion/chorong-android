package net.poksion.chorong.android.task;

import net.poksion.chorong.android.store.ObjectStore;

public interface ObservingTask<ResultT, ListenerT> {
    ObjectStore getStore();
    String getStoreKey();

    boolean isAvailable(ListenerT listener);
    void onChanged(ResultT result, ListenerT listener);
}
