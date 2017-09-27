package net.poksion.chorong.android.samples.domain;

import java.util.ArrayList;
import java.util.List;
import net.poksion.chorong.android.store.ObjectStore;
import net.poksion.chorong.android.store.persistence.DatabaseProxyManager;
import net.poksion.chorong.android.store.persistence.Result;

public abstract class DbRepository {

    public final static String DB_NAME = "sample.db";
    public final static List<Result.Scheme> DB_SCHEMES = new ArrayList<>();

    final static String SAMPLE_DB_TABLE = "sample_table";

    static {
        Result.Scheme scheme = new Result.Scheme(1);
        scheme.addCol(SAMPLE_DB_TABLE, Result.Primitive.STRING, "id", true);
        scheme.addCol(SAMPLE_DB_TABLE, Result.Primitive.STRING, "name", false);
        scheme.addCol(SAMPLE_DB_TABLE, Result.Primitive.STRING, "date", false);

        DB_SCHEMES.add(scheme);
    }

    abstract String getRelatedDbMemeCacheStoreKey();

    private final DatabaseProxyManager databaseProxyManager;

    DbRepository(DatabaseProxyManager databaseProxyManager) {
        this.databaseProxyManager = databaseProxyManager;
    }

    ObjectStore getRelatedObjectStore() {
        return databaseProxyManager.getRelatedObjectStore();
    }
}
