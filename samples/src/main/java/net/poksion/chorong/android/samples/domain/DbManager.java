package net.poksion.chorong.android.samples.domain;

import android.support.annotation.VisibleForTesting;
import android.util.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.poksion.chorong.android.store.ObjectStore;
import net.poksion.chorong.android.store.StoreAccessor;
import net.poksion.chorong.android.store.persistence.DatabaseProxyDefaultFactory;
import net.poksion.chorong.android.store.persistence.DatabaseProxyManager;
import net.poksion.chorong.android.store.persistence.Result;

public class DbManager {

    public final static String DB_NAME = "sample.db";
    public final static List<Result.Scheme> DB_SCHEMES = new ArrayList<>();

    private final static String DB_TABLE = "sample_table";

    static {
        Result.Scheme scheme = new Result.Scheme(1);
        scheme.addCol(DB_TABLE, Result.Primitive.STRING, "id", true);
        scheme.addCol(DB_TABLE, Result.Primitive.STRING, "name", false);
        scheme.addCol(DB_TABLE, Result.Primitive.STRING, "date", false);

        DB_SCHEMES.add(scheme);
    }

    private final ObjectStore objectStore;

    private final static String DB_STATIC_KEY = "sample-db-static-key";
    private final StoreAccessor<Result.Rows> dbSimpleReadingAccessor;
    private final StoreAccessor<Result.Rows> dbSimpleAddingAccessor;

    @VisibleForTesting public final static String DB_CACHE_STATIC_KEY = "sample-db-cache-static-key";
    private final StoreAccessor< List<DbItemModel> > dbCacheAccessor;

    public DbManager(DatabaseProxyManager dbProxyManager, ObjectStore objectStore) {
        dbProxyManager.installDbProxy(DB_STATIC_KEY, DB_TABLE, new DatabaseProxyDefaultFactory());
        dbSimpleReadingAccessor = DatabaseProxyDefaultFactory.makeSimpleReadingAccessor(DB_STATIC_KEY, objectStore);
        dbSimpleAddingAccessor = DatabaseProxyDefaultFactory.makeSimpleAddingAccessor(DB_STATIC_KEY, objectStore);

        dbCacheAccessor = new StoreAccessor<>(DB_CACHE_STATIC_KEY, objectStore);
        this.objectStore = objectStore;
    }

    public List<DbItemModel> readItems(boolean notifyNullResult) {
        return readItemsAndNotify(false, notifyNullResult);
    }

    public List<DbItemModel> addItems(List<DbItemModel> items) {
        dbSimpleAddingAccessor.write(convertModel(items));
        return readItemsAndNotify(true, true);
    }

    ObjectStore getRelatedObjectStore() {
        return objectStore;
    }

    String getRelatedDbMemeCacheStoreKey() {
        return DB_CACHE_STATIC_KEY;
    }

    private Result.Rows convertModel(List<DbItemModel> items) {
        Result.Rows rows = new Result.Rows("id");

        for (DbItemModel item : items) {
            rows.appendRow();

            rows.appendCell("id", Result.Primitive.STRING, item.id);
            rows.appendCell("name", Result.Primitive.STRING, item.name);
            rows.appendCell("date", Result.Primitive.STRING, item.date);
        }

        return rows;
    }

    private List<DbItemModel> convertModel(Result.Rows rows) {
        List<DbItemModel> result = new ArrayList<>();
        int rowCnt = rows.getRowCount();
        for (int i = 0; i < rowCnt; ++i) {
            Map<String, Pair<Result.Primitive, Object>> cols = rows.getRow(i);

            DbItemModel model = new DbItemModel();
            model.id = (String) cols.get("id").second;
            model.name = (String) cols.get("name").second;
            model.date = (String) cols.get("date").second;

            result.add(model);
        }

        return result;
    }

    private List<DbItemModel> readItemsAndNotify(boolean refreshCache, boolean notifyNullResult) {
        List<DbItemModel> result = refreshCache? null : dbCacheAccessor.read();

        if (result == null) {
            result = convertModel(dbSimpleReadingAccessor.read());
        }

        if ( (result == null || result.isEmpty()) && !notifyNullResult) {
            return null;
        }

        dbCacheAccessor.write(result);
        return result;
    }
}
