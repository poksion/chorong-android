package net.poksion.chorong.android.store.persistence;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Pair;
import java.util.List;
import java.util.Map;
import net.poksion.chorong.android.store.ObjectStore;
import net.poksion.chorong.android.store.StoreAccessor;

public class DatabaseProxyDefaultFactory extends DatabaseProxyFactory {

    private static final String SIMPLE_ADDING = "add";
    private static final String SIMPLE_REMOVING = "remove";
    private static final String SIMPLE_READING = "read:";

    private static class SimpleDbProxy implements ObjectStore.PersistenceProxy {

        private final SQLiteOpenHelper dbHelper;
        private final Result.Scheme scheme;
        private final String table;

        SimpleDbProxy(SQLiteOpenHelper dbHelper, Result.Scheme scheme, String table) {
            this.dbHelper = dbHelper;
            this.scheme = scheme;
            this.table = table;
        }

        @Override
        public void setData(String conditions, Object data) {
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
            }
        }

        @Override
        public Object getData(String conditions) {
            if (!conditions.startsWith("read:")) {
                return null;
            }

            String[] wheres = conditions.split(":");
            String where = wheres.length == 2 ? wheres[1] : null;

            synchronized(dbHelper) {
                Result.Rows result = new Result.Rows(scheme.primaryKeys.get(table));
                List<Pair<Result.Primitive, String>> cols = scheme.tables.get(table);

                SQLiteDatabase db = dbHelper.getReadableDatabase();
                Cursor cursor = db.query(table, getAllColNames(cols), where, null, null, null, null);
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
        }
    }

    @Override
    public ObjectStore.PersistenceProxy onNewInstance(SQLiteOpenHelper helper, Result.Scheme scheme, String table) {
        return new SimpleDbProxy(helper, scheme, table);
    }

    public static StoreAccessor<Result.Rows> makeSimpleReadingAccessor(String staticKey, ObjectStore objectStore) {
        return makeDatabaseStoreAccessor(staticKey, SIMPLE_READING, objectStore);
    }

    public static StoreAccessor<Result.Rows> makeSimpleReadingAccessor(String staticKey, ObjectStore objectStore, String where) {
        return makeDatabaseStoreAccessor(staticKey, SIMPLE_READING + where, objectStore);
    }

    public static StoreAccessor<Result.Rows> makeSimpleAddingAccessor(String staticKey, ObjectStore objectStore) {
        return makeDatabaseStoreAccessor(staticKey, SIMPLE_ADDING, objectStore);
    }

    public static StoreAccessor<Result.Rows> makeSimpleRemovingAccessor(String staticKey, ObjectStore objectStore) {
        return makeDatabaseStoreAccessor(staticKey, SIMPLE_REMOVING, objectStore);
    }
}
