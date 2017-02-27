package net.poksion.chorong.android.oauth;

import net.poksion.chorong.android.annotation.NonNull;

public abstract class OAuthLoginPresenterWithActivity implements OAuthLoginPresenter {

    protected abstract void startLoginActivity();

    private final LoginTokenManager loginTokenManager;

    private boolean isLoginTriedPrev = false;
    private String nextRequest;

    protected OAuthLoginPresenterWithActivity(LoginTokenManager loginTokenManager) {
        this.loginTokenManager = loginTokenManager;
    }

    @Override
    public OAuthState getOAuthState() {

        if (isLoginTriedPrev) {
            return OAuthState.OAUTH_ON_RESUME;
        }

        if (isEmptyToken(loginTokenManager.getSavedLoginToken())) {
            return OAuthState.OAUTH_EMPTY;
        }

        return OAuthState.OAUTH_DONE;
    }

    @Override
    public void startOAuth(OAuthState state, @NonNull String nextRequest) {
        if (state != OAuthState.OAUTH_EMPTY) {
            throw new AssertionError("startOAuth should be invoked with OAUTH_EMPTY");
        }

        this.isLoginTriedPrev = true;
        this.nextRequest = nextRequest;

        startLoginActivity();
    }

    @Override
    public String completeOAuth(OAuthState state) {
        if (state != OAuthState.OAUTH_ON_RESUME) {
            throw new AssertionError("completeOAuth should be invoked with OAUTH_ON_RESUME");
        }

        if (!isEmptyToken(loginTokenManager.getSavedLoginToken())) {
            isLoginTriedPrev = false;
            String updatedRequest = nextRequest;
            nextRequest = null;
            return updatedRequest;
        }

        return null;
    }

    @Override
    public void resetOAuth() {
        isLoginTriedPrev = false;
        loginTokenManager.saveLoginToken("");
    }

    @Override
    public String getLoginToken() {
        return loginTokenManager.getSavedLoginToken();
    }

    private boolean isEmptyToken(String loginToken){
        return (loginToken == null || loginToken.length() == 0);
    }
}
