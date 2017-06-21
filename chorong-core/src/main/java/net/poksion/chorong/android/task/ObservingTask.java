package net.poksion.chorong.android.task;

import net.poksion.chorong.android.store.ObjectStore;

public interface ObservingTask<T_Result, T_Listener> {
    ObjectStore getStore();
    String getStoreKey();

    boolean isAvailable(T_Listener listener);
    void onChanged(T_Result result, T_Listener listener);
}
