package net.poksion.chorong.android.api;

import net.poksion.chorong.android.annotation.NonNull;

public interface PicasaWebApi {
    class Result extends ApiResult<String> {
    }

    Result getPhotoUrl(
            @NonNull String loginToken,
            @NonNull String username,
            @NonNull String albumId,
            @NonNull String filename);
}
