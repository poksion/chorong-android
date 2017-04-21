package net.poksion.chorong.android.store.persistence;

import android.util.Pair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Result {

    public enum Primitive {
        STRING("TEXT"),
        LONG("INTEGER"),
        INT("INTEGER"),
        BOOLEAN("INTEGER");

        String dbType;
        Primitive(String dbType) {
            this.dbType = dbType;
        }
    }

    public static class Scheme {
        final int version;

        final Map<String, List<String>> primaryKeys = new HashMap<>();
        final Map<String, List<Pair<Primitive, String>>> tables = new HashMap<>();

        public Scheme(int version) {
            this.version = version;
        }

        public void addCol(String tableName, Primitive type, String name, boolean isPrimary) {
            List<Pair<Primitive, String>> cols = tables.get(tableName);
            if (cols == null) {
                cols = new ArrayList<>();
                tables.put(tableName, cols);
            }

            cols.add(Pair.create(type, name));

            if (isPrimary) {
                List<String> primaryKeyList = primaryKeys.get(tableName);
                if (primaryKeyList == null) {
                    primaryKeyList = new ArrayList<>();
                    primaryKeys.put(tableName, primaryKeyList);
                }

                primaryKeyList.add(name);
            }
        }
    }

    public static class Rows {
        private final String[] primaryKeys;

        private final List<Map<String, Pair<Primitive, Object>>> data = new ArrayList<>();

        public Rows(List<String> primaryKeys) {
            int cnt = primaryKeys.size();
            this.primaryKeys = new String[cnt];
            for (int i = 0; i < cnt; ++i) {
                this.primaryKeys[i] = primaryKeys.get(i);
            }
        }

        public Rows(String... primaryKeys) {
            this.primaryKeys = primaryKeys;
        }

        public String[] getPrimaryKeys() {
            return primaryKeys;
        }

        public void appendRow() {
            data.add(new HashMap<String, Pair<Primitive, Object>>());
        }

        public int getRowCount() {
            return data.size();
        }

        public Map<String, Pair<Primitive, Object>> getRow(int rowIdx) {
            return data.get(rowIdx);
        }

        public void appendCell(String colName, Primitive type, Object item) {
            int cnt = data.size();
            if (cnt == 0) {
                throw new IndexOutOfBoundsException("The row should exist before appending cell");
            }

            data.get(cnt-1).put(colName, Pair.create(type, item));
        }

        public void clear() {
            data.clear();
        }
    }
}
