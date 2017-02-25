package net.poksion.chorong.android.api;

public interface PicasaWebApi {
    class Result {
        public boolean validToken = true;
        public String photoUrl;
    }

    Result getPhotoUrl(String loginToken, String username, String albumId, String filename);
}
