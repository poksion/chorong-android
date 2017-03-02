package net.poksion.chorong.android.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class GoogleSheetApiTest {

    @Test
    public void result_by_name_should_be_empty_without_valid_token() {
        GoogleSheetApi googleSheetApi = new GoogleSheetApiImpl();
        GoogleSheetApi.Result result = googleSheetApi.getResultByName("dummy-token", "dummy-name", "dummy-sheet-name", -1, -1, -1);

        assertThat(result.validToken).isFalse();
        assertThat(result.rows).isEmpty();
    }

    @Test
    public void public_sheet_should_be_acquired_by_id() {
        GoogleSheetApi googleSheetApi = new GoogleSheetApiImpl("chorong-api-test");

        // 3 column table
        // 2 row in each page (totally 3 rows now)
        // request second page (idx 1)
        GoogleSheetApi.Result result = googleSheetApi.getResultById("", "1TYYg55nm-T0LqqnRi6zycz74f41pNDlAaBc8q4d-epc", "", 3, 2, 1);

        assertThat(result.validToken).isTrue();

        // total 3 row
        // 2 pages and each page has 1 row
        assertThat(result.paging).isTrue();
        assertThat(result.rows.size()).isEqualTo(1);
        assertThat(result.lastPageHint).isTrue();
    }
}
