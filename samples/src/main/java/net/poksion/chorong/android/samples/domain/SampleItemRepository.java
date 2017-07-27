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

public class SampleItemRepository extends DbRepository {

    private final static String SAMPLE_DB_STATIC_KEY = "sample-db-static-key";
    private final StoreAccessor<Result.Rows> dbSimpleReadingAccessor;
    private final StoreAccessor<Result.Rows> dbSimpleAddingAccessor;

    @VisibleForTesting public final static String SAMPLE_DB_CACHE_STATIC_KEY = "sample-db-cache-static-key";
    private final StoreAccessor<List<SampleItem>> dbCacheAccessor;

    public SampleItemRepository(DatabaseProxyManager dbProxyManager, ObjectStore objectStore) {
        super(objectStore);

        dbProxyManager.installDbProxy(SAMPLE_DB_STATIC_KEY, SAMPLE_DB_TABLE, new DatabaseProxyDefaultFactory());
        dbSimpleReadingAccessor = DatabaseProxyDefaultFactory.makeSimpleReadingAccessor(SAMPLE_DB_STATIC_KEY, objectStore);
        dbSimpleAddingAccessor = DatabaseProxyDefaultFactory.makeSimpleAddingAccessor(SAMPLE_DB_STATIC_KEY, objectStore, false);

        dbCacheAccessor = new StoreAccessor<>(SAMPLE_DB_CACHE_STATIC_KEY, objectStore);
    }

    @Override
    String getRelatedDbMemeCacheStoreKey() {
        return SAMPLE_DB_CACHE_STATIC_KEY;
    }

    public List<SampleItem> findAll() {
        return convertModel(dbSimpleReadingAccessor.read());
    }

    public SampleItem find(String id) {
        StoreAccessor<Result.Rows> storeAccessor =  DatabaseProxyDefaultFactory.makeSimpleReadingAccessor(SAMPLE_DB_STATIC_KEY, objectStore, "id=" + id);
        List<SampleItem> result = convertModel(storeAccessor.read());
        if (result.isEmpty()) {
            return null;
        }

        return result.get(0);
    }

    public void storeAll(List<SampleItem> items) {
        dbSimpleAddingAccessor.write(convertModel(items));
        dbCacheAccessor.write(items);
    }

    private Result.Rows convertModel(List<SampleItem> items) {
        Result.Rows rows = new Result.Rows("id");

        for (SampleItem item : items) {
            rows.appendRow();

            rows.appendCell("id", Result.Primitive.STRING, item.id);
            rows.appendCell("name", Result.Primitive.STRING, item.name);
            rows.appendCell("date", Result.Primitive.STRING, item.date);
        }

        return rows;
    }

    private List<SampleItem> convertModel(Result.Rows rows) {
        List<SampleItem> result = new ArrayList<>();
        int rowCnt = rows.getRowCount();
        for (int i = 0; i < rowCnt; ++i) {
            Map<String, Pair<Result.Primitive, Object>> cols = rows.getRow(i);

            SampleItem model = new SampleItem();
            model.id = (String) cols.get("id").second;
            model.name = (String) cols.get("name").second;
            model.date = (String) cols.get("date").second;

            result.add(model);
        }

        return result;
    }
}
