package net.poksion.chorong.android.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class GoogleSheetApiTest {

    @Test
    public void testInvalidToken() {
        GoogleSheetApi googleSheetApi = new GoogleSheetApiImpl("chorong-api-test");
        GoogleSheetApi.Result result = googleSheetApi.getValues("", "dummy-name", "dummy-sheet-name", -1, -1, -1);

        assertThat(result.validToken).isFalse();
    }
}
