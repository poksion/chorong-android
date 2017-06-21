package net.poksion.chorong.android.samples.domain;

import java.util.List;
import net.poksion.chorong.android.presenter.BaseView;
import net.poksion.chorong.android.store.ObjectStore;
import net.poksion.chorong.android.task.ObservingTask;

public abstract class DbObservingTask<T_View extends BaseView> implements ObservingTask<List<DbItemModel>, T_View> {
    private final DbManager dbManager;
    private final ObjectStore objectStore;

    public DbObservingTask(DbManager dbManager, ObjectStore objectStore) {
        this.dbManager = dbManager;
        this.objectStore = objectStore;
    }

    @Override
    public ObjectStore getStore() {
        return objectStore;
    }

    @Override
    public String getStoreKey() {
        return dbManager.getDbCacheStaticKey();
    }

    @Override
    public boolean isAvailable(T_View view) {
        return !view.isFinishing();
    }
}
