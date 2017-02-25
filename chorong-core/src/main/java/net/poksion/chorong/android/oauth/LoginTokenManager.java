package net.poksion.chorong.android.oauth;

public interface LoginTokenManager {
    String getSavedLoginToken();
    boolean saveLoginToken(String token);
}
