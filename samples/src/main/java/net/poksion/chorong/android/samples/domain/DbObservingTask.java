package net.poksion.chorong.android.samples.domain;

import java.util.List;
import net.poksion.chorong.android.presenter.BaseView;
import net.poksion.chorong.android.store.ObjectStore;
import net.poksion.chorong.android.task.ObservingTask;

public abstract class DbObservingTask<T_View extends BaseView> implements ObservingTask<List<DbItemModel>, T_View> {
    private final DbManager dbManager;

    public DbObservingTask(DbManager dbManager) {
        this.dbManager = dbManager;
    }

    @Override
    public ObjectStore getStore() {
        return dbManager.getRelatedObjectStore();
    }

    @Override
    public String getStoreKey() {
        return dbManager.getRelatedDbMemeCacheStoreKey();
    }

    @Override
    public boolean isAvailable(T_View view) {
        return !view.isFinishing();
    }
}
