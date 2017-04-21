package net.poksion.chorong.android.store.persistence;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Pair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.poksion.chorong.android.store.ObjectStore;
import net.poksion.chorong.android.store.ObjectStoreApplication;
import net.poksion.chorong.android.store.StoreAccessor;

public class DatabaseProxyManager {

    private static final String SIMPLE_ADDING = "addItemAndGetAll";
    private static final String SIMPLE_REMOVING = "removeItemAndGetAll";

    private static class SimpleDbProxy implements ObjectStore.PersistenceProxy {

        private final DbHelper dbHelper;
        private final String table;

        SimpleDbProxy(DbHelper dbHelper, String table) {
            this.dbHelper = dbHelper;
            this.table = table;
        }

        @Override
        public void setData(String conditions, Object data) {
            if (conditions.equals("")) {
                return;
            }

            synchronized(dbHelper) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                Result.Rows rows = (Result.Rows) data;
                String[] primaryKeys = rows.getPrimaryKeys();

                int rowCnt = rows.getRowCount();
                for (int i = 0; i < rowCnt; ++i) {
                    Map<String, Pair<Result.Primitive, Object>> row = rows.getRow(i);
                    setSimple(db, conditions, primaryKeys, row);
                }
            }
        }

        private void setSimple(SQLiteDatabase db, String conditions, String[] primaryKeys, Map<String, Pair<Result.Primitive, Object>> row) {
            String whereStatement = whereClause(primaryKeys);
            String[] whereArgs = whereArgs(row, primaryKeys);

            //noinspection IfCanBeSwitch
            if (conditions.equals(SIMPLE_ADDING)) {
                ContentValues contentValues = new ContentValues();

                for (Map.Entry<String, Pair<Result.Primitive, Object>> value : row.entrySet()) {
                    switch(value.getValue().first) {
                        case STRING:
                            contentValues.put(value.getKey(), (String) value.getValue().second);
                            break;
                        case LONG:
                            contentValues.put(value.getKey(), (Long) value.getValue().second);
                            break;
                        case INT:
                            contentValues.put(value.getKey(), (Integer) value.getValue().second);
                            break;
                        case BOOLEAN:
                            contentValues.put(value.getKey(), (Boolean) value.getValue().second);
                            break;
                    }
                }

                int ret = db.update(table, contentValues, whereStatement, whereArgs);
                if (ret == 0) {
                    db.insert(table, null, contentValues);
                }

            } else if (conditions.equals(SIMPLE_REMOVING)) {
                db.delete(table, whereStatement, whereArgs);
            } else {
                throw new IllegalArgumentException("conditions should be simple adding or simple removing");
            }
        }

        @Override
        public Object getData(String conditions) {
            synchronized(dbHelper) {
                if (conditions.equals("") || conditions.equals(SIMPLE_ADDING) || conditions.equals(SIMPLE_REMOVING)) {
                    Result.Rows result = new Result.Rows(dbHelper.getCurrentScheme().primaryKeys.get(table));
                    List<Pair<Result.Primitive, String>> cols = dbHelper.getCurrentScheme().tables.get(table);

                    SQLiteDatabase db = dbHelper.getReadableDatabase();
                    Cursor cursor = db.query(table, getAllColNames(cols), null, null, null, null, null);
                    if(cursor != null){
                        cursor.moveToFirst();
                        while(!cursor.isAfterLast()){
                            result.appendRow();

                            for (Pair<Result.Primitive, String> col : cols) {
                                int colIdx = cursor.getColumnIndex(col.second);
                                switch(col.first) {
                                    case STRING:
                                        result.appendCell(col.second, col.first, cursor.getString(colIdx));
                                        break;
                                    case LONG:
                                        result.appendCell(col.second, col.first, cursor.getLong(colIdx));
                                        break;
                                    case INT:
                                        result.appendCell(col.second, col.first, cursor.getInt(colIdx));
                                        break;
                                    case BOOLEAN:
                                        result.appendCell(col.second, col.first, cursor.getInt(colIdx) == 1);
                                        break;
                                }

                            }
                            cursor.moveToNext();
                        }
                        cursor.close();
                    }

                    return result;
                }

                throw new IllegalArgumentException("conditions should be simple adding or simple removing");
            }
        }
    }

    private final ObjectStore objectStore;
    private final List<Result.Scheme> schemes;
    private final Result.Scheme currentScheme;

    private final DbHelper dbHelper;

    private class DbHelper extends SQLiteOpenHelper {

        DbHelper(Context context, String name) {
            super(context, name, null, currentScheme.version);
        }

        Result.Scheme getCurrentScheme() {
            return currentScheme;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            for (Map.Entry<String, List<Pair<Result.Primitive, String>>> table :  currentScheme.tables.entrySet()) {
                db.execSQL( createTableStatement(
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
                    db.execSQL( createTableStatement(
                            table.getKey(),
                            cols,
                            newScheme.primaryKeys.get(table.getKey())) );
                } else {
                    for (Pair<Result.Primitive, String> col : table.getValue()) {
                        db.execSQL(alterSqlStatement(table.getKey(), col, true));
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
                        db.execSQL(alterSqlStatement(table.getKey(), col, false));
                    }
                }
            }
        }
    }

    public DatabaseProxyManager(ObjectStoreApplication objectStoreApplication, String dbName, List<Result.Scheme> schemes) {
        this(objectStoreApplication, objectStoreApplication, dbName, schemes);
    }

    public DatabaseProxyManager(ObjectStore objectStore, Application application, String dbName, List<Result.Scheme> schemes) {
        this.objectStore = objectStore;
        this.schemes = schemes;

        currentScheme = getScheme(schemes, -1);
        dbHelper = new DbHelper(application.getApplicationContext(), dbName);
    }

    public void installSimpleProxy(String staticKey, String tableName) {
        SimpleDbProxy simpleDbProxy = new SimpleDbProxy(dbHelper, tableName);
        objectStore.setPersistenceProxy(staticKey, simpleDbProxy);
    }


    public StoreAccessor<Result.Rows> makeSimpleAddingAccessor(String staticKey) {
        return makeSimpleAccessor(staticKey, SIMPLE_ADDING);
    }

    public StoreAccessor<Result.Rows> makeSimpleRemovingAccessor(String staticKey) {
        return makeSimpleAccessor(staticKey, SIMPLE_REMOVING);
    }

    public StoreAccessor<Result.Rows> makeSimpleAccessor(String staticKey, String condition) {
        return new StoreAccessor<>(new ObjectStore.Key(staticKey, condition), objectStore);
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

    static String createTableStatement(String tableName, List<Pair<Result.Primitive, String>> cols, List<String> primaryKeys) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ").append(tableName).append(" (");

        for (Pair<Result.Primitive, String> col : cols) {
            sb.append(col.second).append(" ").append(col.first.dbType).append(nullableSqlStatement(col.second, primaryKeys));
        }

        sb.append(primaryKeySqlStatement(primaryKeys));
        sb.append(");");

        return sb.toString();
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
                    if (isInSrc(srcCols, resultCol.second)) {
                        continue;
                    }

                    cols.add(resultCol);
                }
            }
        }
        return result;
    }

    static private boolean isInSrc(List<Pair<Result.Primitive, String>> src, String key) {
        for (Pair<Result.Primitive, String> col : src) {
            if (col.second.equals(key)) {
                return true;
            }
        }

        return false;
    }

    static private String nullableSqlStatement(String colName, List<String> primaryKeys) {
        for (String primaryKey : primaryKeys) {
            if (primaryKey.equals(colName)) {
                return " NOT NULL, ";
            }
        }

        return ", ";
    }

    static String primaryKeySqlStatement(List<String> primaryKeys) {
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

    static String alterSqlStatement(String table, Pair<Result.Primitive, String> col, boolean add) {
        if (add) {
            return "ALTER TABLE " + table + " ADD COLUMN " + col.second + " " + col.first.dbType + ";";
        } else {
            return "ALTER TABLE " + table + " DROP COLUMN " + col.second + ";";
        }
    }

    static String whereClause(String[] primaryKeys) {
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

    static String[] whereArgs(Map<String, Pair<Result.Primitive, Object>> row, String[] primaryKeys) {
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

    static String getAsStringValue(Pair<Result.Primitive, Object> typedValue) {
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

    static String[] getAllColNames(List<Pair<Result.Primitive, String>> cols) {

        int cnt = cols.size();
        String[] allCols = new String[cnt];
        for (int i = 0; i < cnt; ++i) {
            allCols[i] = cols.get(i).second;
        }

        return allCols;
    }
}
