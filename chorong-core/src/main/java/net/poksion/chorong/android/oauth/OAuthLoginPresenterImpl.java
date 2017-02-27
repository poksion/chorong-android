package net.poksion.chorong.android.oauth;

import net.poksion.chorong.android.annotation.NonNull;

public abstract class OAuthLoginPresenterImpl implements OAuthLoginPresenter {

    protected abstract void startLoginActivity();

    private final LoginTokenManager loginTokenManager;

    private boolean isLoginTriedPrev = false;
    private String nextRequest;


    public OAuthLoginPresenterImpl(LoginTokenManager loginTokenManager) {
        this.loginTokenManager = loginTokenManager;
    }

    @Override
    public boolean isEmptyTokenButNotLoginTried() {
        return (!isLoginTriedPrev && isEmptyToken(loginTokenManager.getSavedLoginToken()));
    }

    @Override
    public void setLoginTriedAndStartActivity(@NonNull String nextRequest) {
        this.isLoginTriedPrev = true;
        this.nextRequest = nextRequest;

        startLoginActivity();
    }

    @Override
    public boolean checkOnResumeLoginTriedAndBack() {
        return isLoginTriedPrev;
    }

    @Override
    public String updateOnResumeNextRequest() {
        if (!isEmptyToken(loginTokenManager.getSavedLoginToken())) {
            isLoginTriedPrev = false;
            String updatedRequest = nextRequest;
            nextRequest = null;
            return updatedRequest;
        }

        return null;
    }

    @Override
    public String getLoginToken() {
        return loginTokenManager.getSavedLoginToken();
    }

    @Override
    public void resetOAuth() {
        isLoginTriedPrev = false;
        loginTokenManager.saveLoginToken("");
    }

    private boolean isEmptyToken(String loginToken){
        return (loginToken == null || loginToken.length() == 0);
    }
}
