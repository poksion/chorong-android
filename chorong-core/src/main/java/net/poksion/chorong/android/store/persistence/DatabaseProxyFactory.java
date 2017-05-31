package net.poksion.chorong.android.store.persistence;

import android.database.sqlite.SQLiteOpenHelper;
import android.util.Pair;
import java.util.List;
import java.util.Map;
import net.poksion.chorong.android.store.ObjectStore;
import net.poksion.chorong.android.store.StoreAccessor;

public abstract class DatabaseProxyFactory {

    protected abstract ObjectStore.PersistenceProxy onNewInstance(SQLiteOpenHelper helper, Result.Scheme scheme, String table);

    protected static String whereClause(String[] primaryKeys) {
        int cnt = primaryKeys.length;
        if (cnt == 0) {
            throw new IllegalArgumentException("the primary key should be at least one on writing");
        }

        StringBuilder sb = new StringBuilder(primaryKeys[0] + "=?");

        for (int i = 1; i < cnt; ++i) {
            sb.append(" and ").append(primaryKeys[i]).append("=?");
        }

        return sb.toString();
    }

    protected static String[] whereArgs(Map<String, Pair<Result.Primitive, Object>> row, String[] primaryKeys) {
        String[] args = new String[primaryKeys.length];

        for (int i = 0; i < primaryKeys.length; ++i) {
            String primaryKey = primaryKeys[i];
            for (Map.Entry<String, Pair<Result.Primitive, Object>> col : row.entrySet()) {
                if (col.getKey().equals(primaryKey)) {
                    args[i] = getAsStringValue(col.getValue());
                }
            }
        }

        return args;
    }

    protected static String getAsStringValue(Pair<Result.Primitive, Object> typedValue) {
        if (typedValue.first == Result.Primitive.STRING) {
            return (String) typedValue.second;
        }

        if (typedValue.first == Result.Primitive.BOOLEAN) {
            if ((Boolean)typedValue.second) {
                return "1";
            }
            return "0";
        }

        // long or integer
        if (typedValue.second instanceof Long) {
            return Long.toString((Long) typedValue.second);
        } else {
            return Integer.toString((Integer) typedValue.second);
        }
    }

    protected static String[] getAllColNames(List<Pair<Result.Primitive, String>> cols) {

        int cnt = cols.size();
        String[] allCols = new String[cnt];
        for (int i = 0; i < cnt; ++i) {
            allCols[i] = cols.get(i).second;
        }

        return allCols;
    }

    protected static StoreAccessor<Result.Rows> makeDatabaseStoreAccessor(String staticKey, String condition, ObjectStore objectStore) {
        return new StoreAccessor<>(new ObjectStore.Key(staticKey, condition), objectStore);
    }

}
