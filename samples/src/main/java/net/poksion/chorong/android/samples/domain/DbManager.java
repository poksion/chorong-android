package net.poksion.chorong.android.samples.domain;

import android.util.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.poksion.chorong.android.store.StoreAccessor;
import net.poksion.chorong.android.store.persistence.DatabaseProxyManager;
import net.poksion.chorong.android.store.persistence.Result;

public class DbManager {
    private final static String DB_TABLE = "sample_table";

    public final static String DB_NAME = "sample.db";
    public final static List<Result.Scheme> DB_SCHEMES = new ArrayList<>();
    static {
        Result.Scheme scheme = new Result.Scheme(1);
        scheme.addCol(DB_TABLE, Result.Primitive.STRING, "id", true);
        scheme.addCol(DB_TABLE, Result.Primitive.STRING, "name", false);
        scheme.addCol(DB_TABLE, Result.Primitive.STRING, "date", false);

        DB_SCHEMES.add(scheme);
    }


    private final static String DB_STATIC_KEY = "sample-db-static-key";
    private final StoreAccessor<Result.Rows> dbAddingAccessor;

    // using memory cache for db changed observer
    private final static String DB_MEM_CACHE_KEY = "sample-db-mem-cache-key";
    private final StoreAccessor<Result.Rows> dbMemCacheAccessor;

    public DbManager(DatabaseProxyManager dbProxyManager) {
        dbProxyManager.installSimpleProxy(DB_STATIC_KEY, DB_TABLE);
        dbAddingAccessor = dbProxyManager.makeSimpleAddingAccessor(DB_STATIC_KEY);

        dbMemCacheAccessor = dbProxyManager.makeSimpleAccessor(DB_MEM_CACHE_KEY, "");
    }

    public List<DbItemModel> addItems(List<DbItemModel> items) {
        dbAddingAccessor.write(convertModel(items));

        Result.Rows rows = dbAddingAccessor.read();
        dbMemCacheAccessor.write(rows);

        return convertModel(rows);
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
}