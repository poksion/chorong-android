package net.poksion.chorong.android.api;

import com.google.gdata.client.photos.PicasawebService;
import com.google.gdata.data.MediaContent;
import com.google.gdata.data.photos.AlbumFeed;
import com.google.gdata.data.photos.GphotoEntry;
import com.google.gdata.util.ServiceException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import net.poksion.chorong.android.annotation.NonNull;
import net.poksion.chorong.android.annotation.Nullable;

public class PicasaWebApiImpl extends ApiTemplate implements PicasaWebApi {

    private final PicasawebService service;

    private String cachedToken;
    private final Map<String, String> cached = new HashMap<>();

    public PicasaWebApiImpl() {
        this(null);
    }

    public PicasaWebApiImpl(@Nullable String applicationName) {
        service = createService(PicasawebService.class, applicationName);
    }

    @Override
    public Result getPhotoUrl(
            @NonNull final String loginToken,
            @NonNull final String username,
            @NonNull final String albumId,
            @NonNull final String filename) {

        if (loginToken == null || loginToken.length() == 0 || !loginToken.equals(cachedToken)) {
            cachedToken = loginToken;
            cached.clear();
        }

        final String cacheKey = albumId + "/" + filename;
        String link = cached.get(cacheKey);
        if (link != null && link.length() > 0) {
            Result result = new Result();
            result.data = link;
            return result;
        }

        Command<Result> command = new Command<Result>() {
            @Override
            public Result onTry() throws ServiceException, URISyntaxException, IOException {
                setBearerToken(service, loginToken);

                URL feedUrl = new URL("https://picasaweb.google.com/data/feed/api/user/" + username + "/albumid/" + albumId);
                AlbumFeed feed = service.getFeed(feedUrl, AlbumFeed.class);

                Result result = getEmptyResult();
                if (feed == null) {
                    return result;
                }

                for(GphotoEntry<?> photo : feed.getEntries()) {
                    String photoFileName = photo.getTitle().getPlainText();

                    MediaContent content = (MediaContent)photo.getContent();
                    String photoRealLink = content.getUri();

                    cached.put(albumId + "/" + photoFileName, photoRealLink);
                }

                result.data = cached.get(cacheKey);
                return result;
            }

            @Override
            public Result getEmptyResult() {
                return new Result();
            }
        };

        return invoke(command);
    }
}
