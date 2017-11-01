package net.poksion.chorong.android.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.gdata.client.Service;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.IFeed;
import com.google.gdata.util.ServiceException;
import java.io.IOException;
import java.net.URL;
import org.junit.Test;

public class GoogleSheetApiTest {

    private enum TestApiMode {
        RETURN_NULL_FEED
    }

    private static class TestGoogleSheetApi extends GoogleSheetApiImpl {

        private final TestApiMode testApiMode;

        TestGoogleSheetApi(TestApiMode testApiMode) {
            this.testApiMode = testApiMode;
        }

        @Override
        <T extends Service> T createService(Class<T> serviceClass, String applicationName) {
            Service service = new SpreadsheetService(null) {
                @Override
                public <F extends IFeed> F getFeed(URL feedUrl, Class<F> feedClass) throws IOException, ServiceException {
                    if (testApiMode == TestApiMode.RETURN_NULL_FEED) {
                        return null;
                    }

                    return null;
                }
            };

            //noinspection unchecked
            return (T)service;
        }
    }

    @Test
    public void result_by_name_works_only_private_token_mode() {
        GoogleSheetApi googleSheetApi = new TestGoogleSheetApi(TestApiMode.RETURN_NULL_FEED);
        GoogleSheetApi.Result result = googleSheetApi.getSheetByName("dummy-token", "dummy-name", "dummy-sheet-name", -1, -1, -1);

        assertThat(result.error).isEqualTo(ApiResult.Error.None);
        assertThat(result.data).isEmpty();

        result = googleSheetApi.getSheetById("dummy-token", "dummy-id", "dummy-sheet-name", -1, -1, -1);
        assertThat(result.error).isEqualTo(ApiResult.Error.None);
        assertThat(result.data).isEmpty();
    }

    @Test
    public void result_by_name_should_be_empty_without_valid_token() {
        GoogleSheetApi googleSheetApi = new GoogleSheetApiImpl();
        GoogleSheetApi.Result result = googleSheetApi.getSheetByName("dummy-token", "dummy-name", "dummy-sheet-name", -1, -1, -1);

        assertThat(result.error).isEqualTo(ApiResult.Error.Auth);
        assertThat(result.data).isEmpty();
    }

    @Test
    public void public_sheet_should_be_acquired_by_id() {
        GoogleSheetApi googleSheetApi = new GoogleSheetApiImpl("chorong-api-test");

        // https://docs.google.com/spreadsheets/d/e/2PACX-1vSjshXnplo1Psn6H-90QRsDlZ5Ecnj0nMuZnMgnd0ko1BKZptlTZCUhsliGveXDRpiE3ED_LEGZlS0A/pubhtml
        // 3 column table
        // 2 row in each page (totally 3 rows now)
        // request second page (idx 1)
        GoogleSheetApi.Result result = googleSheetApi.getSheetById("", "1TYYg55nm-T0LqqnRi6zycz74f41pNDlAaBc8q4d-epc", "", 3, 2, 1);

        assertThat(result.error).isEqualTo(ApiResult.Error.None);

        // total 3 row
        // 2 pages and each page has 1 row
        assertThat(result.paging).isTrue();
        assertThat(result.data.size()).isEqualTo(1);
        assertThat(result.lastPageHint).isTrue();
    }
}
