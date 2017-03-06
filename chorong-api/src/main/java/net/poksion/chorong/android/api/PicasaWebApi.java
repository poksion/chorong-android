package net.poksion.chorong.android.api;

public interface PicasaWebApi {
    class Result extends ApiResult<String> {
    }

    Result getPhotoUrl(String loginToken, String username, String albumId, String filename);
}
