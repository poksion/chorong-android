package net.poksion.chorong.android.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.IFeed;
import com.google.gdata.util.ServiceException;
import java.io.IOException;
import java.net.URL;
import org.junit.Test;

public class GoogleSheetApiTest {

    enum TestApiMode {
        RETURN_NULL_FEED
    }

    private static class TestGoogleSheetApi extends GoogleSheetApiImpl {

        private final TestApiMode testApiMode;

        TestGoogleSheetApi(TestApiMode testApiMode) {
            this.testApiMode = testApiMode;
        }

        @Override
        SpreadsheetService createSpreadsheetService(String loginToken) {
            return new SpreadsheetService(null) {
                @Override
                public <F extends IFeed> F getFeed(URL feedUrl, Class<F> feedClass) throws IOException, ServiceException {
                    return null;
                }
            };
        }
    }

    @Test
    public void result_by_name_works_only_private_token_mode() {
        GoogleSheetApi googleSheetApi = new TestGoogleSheetApi(TestApiMode.RETURN_NULL_FEED);
        GoogleSheetApi.Result result = googleSheetApi.getResultByName("dummy-token", "dummy-name", "dummy-sheet-name", -1, -1, -1);

        assertThat(result.validToken).isTrue();
        assertThat(result.rows).isEmpty();
    }

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
