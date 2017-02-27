package net.poksion.chorong.android.oauth;

import net.poksion.chorong.android.annotation.NonNull;

public interface OAuthLoginPresenter {

    enum OAuthState {
        OAUTH_EMPTY,
        OAUTH_ON_RESUME,
        OAUTH_DONE
    }

    OAuthState getOAuthState();

    void startOAuth(OAuthState state, @NonNull String nextRequest);
    String completeOAuth(OAuthState state);
    void resetOAuth();

    String getLoginToken();

}
