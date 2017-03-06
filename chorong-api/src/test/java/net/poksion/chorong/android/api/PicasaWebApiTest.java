package net.poksion.chorong.android.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.gdata.client.Service;
import com.google.gdata.client.photos.PicasawebService;
import com.google.gdata.data.IFeed;
import com.google.gdata.util.ServiceException;
import java.io.IOException;
import java.net.URL;
import org.junit.Test;

public class PicasaWebApiTest {

    private enum TestApiMode {
        RETURN_NULL_FEED
    }

    private static class TestPicasaWebApi extends PicasaWebApiImpl {

        private final TestApiMode testApiMode;

        TestPicasaWebApi(TestApiMode testApiMode) {
            this.testApiMode = testApiMode;
        }

        @Override
        <T extends Service> T createService(Class<T> serviceClass, String applicationName) {
            Service service = new PicasawebService(null) {
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
    public void result_data_is_null_if_feed_is_null() {
        PicasaWebApi picasaWebApi = new TestPicasaWebApi(TestApiMode.RETURN_NULL_FEED);
        PicasaWebApi.Result result = picasaWebApi.getPhotoUrl("dummy-token", "dummy-user", "dummy-album-id", "dummy-file-name");

        assertThat(result.error).isEqualTo(ApiResult.Error.None);
        assertThat(result.data).isNull();
    }
}
