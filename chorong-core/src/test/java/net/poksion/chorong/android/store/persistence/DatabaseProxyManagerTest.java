package net.poksion.chorong.android.store.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import android.content.Context;
import android.util.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.poksion.chorong.android.store.ObjectStoreApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE)
public class DatabaseProxyManagerTest {

    private DatabaseProxyManager databaseProxyManager;
    private List<Result.Scheme> schemes = new ArrayList<>();

    @Before
    public void setUp() {
        ObjectStoreApplication objectStoreApplication = new ObjectStoreApplication() {
            @Override
            public Context getApplicationContext() {
                return RuntimeEnvironment.application.getApplicationContext();
            }
        };
        Result.Scheme scheme1 = new Result.Scheme(1);
        scheme1.addCol("test_table", Result.Primitive.STRING, "id", true);
        scheme1.addCol("test_table", Result.Primitive.STRING, "name", false);

        Result.Scheme scheme2 = new Result.Scheme(2);
        scheme2.addCol("test_table", Result.Primitive.STRING, "id", true);
        scheme2.addCol("test_table", Result.Primitive.STRING, "name", false);
        scheme2.addCol("test_table", Result.Primitive.STRING, "date", false);

        scheme2.addCol("test_table2", Result.Primitive.STRING, "id_1", true);
        scheme2.addCol("test_table2", Result.Primitive.STRING, "id_2", true);
        scheme2.addCol("test_table2", Result.Primitive.STRING, "name", false);

        schemes.clear();
        schemes.add(scheme1);
        schemes.add(scheme2);

        databaseProxyManager = new DatabaseProxyManager(objectStoreApplication, "test.db", schemes);
    }

    @Test
    public void test_create_table_statement() {
        Result.Scheme scheme = schemes.get(0);
        for (Map.Entry<String, List<Pair<Result.Primitive, String>>> table :  scheme.tables.entrySet()) {
            String statement = DatabaseProxyManager.createTableStatement(
                    table.getKey(),
                    table.getValue(),
                    scheme.primaryKeys.get(table.getKey()) );

            String expected = "CREATE TABLE test_table (id TEXT NOT NULL, name TEXT, PRIMARY KEY(id));";
            assertThat(statement).isEqualTo(expected);
        }

    }

    @Test
    public void test_primary_key_statement() {
        List<String> primaryKeys = new ArrayList<>();
        primaryKeys.add("id_1");
        primaryKeys.add("id_2");

        String statement = DatabaseProxyManager.primaryKeySqlStatement(primaryKeys);
        String expected = "PRIMARY KEY(id_1,id_2)";
        assertThat(statement).isEqualTo(expected);

        Result.Rows rows = new Result.Rows(primaryKeys);
        rows.appendRow();
        rows.appendCell("id_1", Result.Primitive.STRING, "01");
        rows.appendCell("id_2", Result.Primitive.STRING, "001");
        
        String whereClause = DatabaseProxyManager.whereClause(rows.getPrimaryKeys());
        String expectedClause = "id_1=? and id_2=?";
        assertThat(whereClause).isEqualTo(expectedClause);

        String[] whereArgs = DatabaseProxyManager.whereArgs(rows.getRow(0), rows.getPrimaryKeys());
        assertThat(whereArgs[0]).isEqualTo("01");
        assertThat(whereArgs[1]).isEqualTo("001");
    }

    @Test
    public void test_get_only_on_target() {
        Map<String, List<Pair<Result.Primitive, String>>> diff = DatabaseProxyManager.getOnlyOnTarget(schemes.get(0), schemes.get(1));
        assertThat(diff.size()).isEqualTo(2);

        List<Pair<Result.Primitive, String>> testTableDiffCols = diff.get("test_table");
        assertThat(testTableDiffCols.size()).isEqualTo(1);
        assertThat(testTableDiffCols.get(0).second).isEqualTo("date");

        String alterStatementAdd = DatabaseProxyManager.alterSqlStatement("test_table", testTableDiffCols.get(0), true);
        assertThat(alterStatementAdd).isEqualTo("ALTER TABLE test_table ADD COLUMN date TEXT;");
        String alterStatementDrop = DatabaseProxyManager.alterSqlStatement("test_table", testTableDiffCols.get(0), false);
        assertThat(alterStatementDrop).isEqualTo("ALTER TABLE test_table DROP COLUMN date;");

        List<Pair<Result.Primitive, String>> testTable2DiffCols = diff.get("test_table2");
        assertThat(testTable2DiffCols.size()).isEqualTo(3);
        assertThat(testTable2DiffCols.size()).isEqualTo(schemes.get(1).tables.get("test_table2").size());

        String[] allCols = DatabaseProxyManager.getAllColNames(testTable2DiffCols);
        assertThat(allCols[0]).isEqualTo("id_1");
        assertThat(allCols[1]).isEqualTo("id_2");
        assertThat(allCols[2]).isEqualTo("name");
    }

    @Test
    public void test_get_as_string_value() {
        Result.Rows rows = new Result.Rows("id");
        try {
            rows.appendCell("id", Result.Primitive.STRING, "001");
            fail("should be appended row");
        } catch(Exception ignore) {}

        rows.appendRow();
        rows.appendCell("id", Result.Primitive.STRING, "001");
        rows.appendCell("is_adult", Result.Primitive.BOOLEAN, false);
        rows.appendCell("age", Result.Primitive.INT, 16);
        rows.appendCell("hits", Result.Primitive.LONG, 654321);

        String idStr = DatabaseProxyManager.getAsStringValue(rows.getRow(0).get("id"));
        assertThat(idStr).isEqualTo("001");
        String isAdultStr = DatabaseProxyManager.getAsStringValue(rows.getRow(0).get("is_adult"));
        assertThat(isAdultStr).isEqualTo("0");
        String ageStr = DatabaseProxyManager.getAsStringValue(rows.getRow(0).get("age"));
        assertThat(ageStr).isEqualTo("16");
        String hitsStr = DatabaseProxyManager.getAsStringValue(rows.getRow(0).get("hits"));
        assertThat(hitsStr).isEqualTo("654321");
    }

    @Test
    public void test_get_scheme() {
        Result.Scheme v2 = DatabaseProxyManager.getScheme(schemes, 2);
        assertThat(v2.version).isEqualTo(2);

        Result.Scheme last = DatabaseProxyManager.getScheme(schemes, -1);
        assertThat(last.version).isEqualTo(2);

        assertThat(DatabaseProxyManager.getScheme(schemes, 3)).isNull();
    }
}
