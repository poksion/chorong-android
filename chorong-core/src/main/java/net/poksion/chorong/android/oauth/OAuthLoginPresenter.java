package net.poksion.chorong.android.oauth;

import net.poksion.chorong.android.annotation.NonNull;

public interface OAuthLoginPresenter {

    boolean isEmptyTokenButNotLoginTried();
    void setLoginTriedAndStartActivity(@NonNull String nextRequest);

    boolean checkOnResumeLoginTriedAndBack();
    String updateOnResumeNextRequest();

    String getLoginToken();
    void resetOAuth();

}
