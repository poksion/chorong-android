package net.poksion.chorong.android.store.persistence;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Pair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.poksion.chorong.android.store.ObjectStore;
import net.poksion.chorong.android.store.ObjectStoreApplication;

public class DatabaseProxyManager extends ProxyManager {

    private final List<Result.Scheme> schemes;
    private final Result.Scheme currentScheme;

    private final DbHelper dbHelper;

    private class DbHelper extends SQLiteOpenHelper {

        DbHelper(Context context, String name) {
            super(context, name, null, currentScheme.version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            for (Map.Entry<String, List<Pair<Result.Primitive, String>>> table :  currentScheme.tables.entrySet()) {
                db.execSQL( sqlCreateTable(
                        table.getKey(),
                        table.getValue(),
                        currentScheme.primaryKeys.get(table.getKey())) );
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Result.Scheme oldScheme = getScheme(schemes, oldVersion);
            Result.Scheme newScheme = getScheme(schemes, newVersion);

            if (oldScheme == null || newScheme == null) {
                return;
            }

            Map<String, List<Pair<Result.Primitive, String>>> alterAddList = getOnlyOnTarget(oldScheme, newScheme);
            for (Map.Entry<String, List<Pair<Result.Primitive, String>>> table : alterAddList.entrySet()) {
                List<Pair<Result.Primitive, String>> cols = newScheme.tables.get(table.getKey());
                if (table.getValue().size() == cols.size()) {
                    db.execSQL( sqlCreateTable(
                            table.getKey(),
                            cols,
                            newScheme.primaryKeys.get(table.getKey())) );
                } else {
                    for (Pair<Result.Primitive, String> col : table.getValue()) {
                        db.execSQL(sqlAlter(table.getKey(), col, true));
                    }
                }
            }

            Map<String, List<Pair<Result.Primitive, String>>> alterDropList = getOnlyOnTarget(newScheme, oldScheme);
            for (Map.Entry<String, List<Pair<Result.Primitive, String>>> table : alterDropList.entrySet()) {
                if (table.getValue().size() == oldScheme.tables.get(table.getKey()).size()) {
                    String statement = "DROP TABLE " + table.getKey() + ";";
                    db.execSQL(statement);
                } else {
                    for (Pair<Result.Primitive, String> col : table.getValue()) {
                        db.execSQL(sqlAlter(table.getKey(), col, false));
                    }
                }
            }
        }
    }

    public DatabaseProxyManager(ObjectStoreApplication objectStoreApplication, String dbName, List<Result.Scheme> schemes) {
        this(objectStoreApplication, objectStoreApplication, dbName, schemes);
    }

    public DatabaseProxyManager(ObjectStore objectStore, Application application, String dbName, List<Result.Scheme> schemes) {
        super(objectStore);

        this.schemes = schemes;

        currentScheme = getScheme(schemes, -1);
        dbHelper = new DbHelper(application.getApplicationContext(), dbName);
    }

    public void installDbProxy(String staticKey, String table, DatabaseProxyFactory factory) {
        getRelatedObjectStore().setPersistenceProxy(staticKey, factory.onNewInstance(dbHelper, currentScheme, table));
    }

    static Result.Scheme getScheme(List<Result.Scheme> schemes, int version) {
        Result.Scheme result = null;
        if (version == -1) {
            result = schemes.get(0);
        }
        for (Result.Scheme scheme : schemes) {
            if (version == -1) {
                if (scheme.version > result.version) {
                    result = scheme;
                }
            } else if (scheme.version == version) {
                return scheme;
            }
        }

        return result;
    }

    static Map<String, List<Pair<Result.Primitive, String>>> getOnlyOnTarget(Result.Scheme src, Result.Scheme target) {
        Map<String, List<Pair<Result.Primitive, String>>> result = new HashMap<>();
        for (Map.Entry<String, List<Pair<Result.Primitive, String>>> table : target.tables.entrySet()) {
            List<Pair<Result.Primitive, String>> srcCols = src.tables.get(table.getKey());
            if (srcCols == null) {
                result.put(table.getKey(), table.getValue());
            } else {
                List<Pair<Result.Primitive, String>> cols = result.get(table.getKey());
                if (cols == null) {
                    cols = new ArrayList<>();
                    result.put(table.getKey(), cols);
                }

                for (Pair<Result.Primitive, String> resultCol : table.getValue()) {
                    if (isInSrc(srcCols, resultCol)) {
                        continue;
                    }

                    cols.add(resultCol);
                }
            }
        }
        return result;
    }

    static private boolean isInSrc(List<Pair<Result.Primitive, String>> src, Pair<Result.Primitive, String> key) {
        for (Pair<Result.Primitive, String> col : src) {
            if (col.first.equals(key.first) && col.second.equals(key.second)) {
                return true;
            }
        }

        return false;
    }

    static String sqlCreateTable(String tableName, List<Pair<Result.Primitive, String>> cols, List<String> primaryKeys) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ").append(tableName).append(" (");

        for (Pair<Result.Primitive, String> col : cols) {
            sb.append(col.second).append(" ").append(col.first.dbType).append(sqlNullable(col.second, primaryKeys));
        }

        sb.append(sqlPrimaryKey(primaryKeys));
        sb.append(");");

        for (String index : sqlIndex(tableName, cols, primaryKeys)) {
            sb.append(index);
        }

        return sb.toString();
    }

    static private String sqlNullable(String colName, List<String> primaryKeys) {
        for (String primaryKey : primaryKeys) {
            if (primaryKey.equals(colName)) {
                return " NOT NULL, ";
            }
        }

        return ", ";
    }

    static private List<String> sqlIndex(String tableName, List<Pair<Result.Primitive, String>> cols, List<String> primaryKeys) {
        List<String> result = new ArrayList<>();
        for (String primaryKey : primaryKeys) {
            for (Pair<Result.Primitive, String> col : cols) {
                if (col.first != Result.Primitive.INT && col.second.equals(primaryKey)) {
                    result.add(" CREATE INDEX " + tableName + "_" + primaryKey + "_idx ON " + tableName + "(" + primaryKey + ");");
                }
            }
        }
        return result;
    }

    static String sqlPrimaryKey(List<String> primaryKeys) {
        StringBuilder sb = new StringBuilder("PRIMARY KEY(");
        int cnt = primaryKeys.size();
        for (int i = 0; i < cnt; ++i) {
            sb.append(primaryKeys.get(i));
            if (i != cnt - 1) {
                sb.append(",");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    static String sqlAlter(String table, Pair<Result.Primitive, String> col, boolean add) {
        if (add) {
            return "ALTER TABLE " + table + " ADD COLUMN " + col.second + " " + col.first.dbType + ";";
        } else {
            return "ALTER TABLE " + table + " DROP COLUMN " + col.second + ";";
        }
    }
}
