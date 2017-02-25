package net.poksion.chorong.android.oauth;

import net.poksion.chorong.android.annotation.NonNull;

public interface OAuthLoginPresenter {

    boolean isEmptyTokenButNotLoginTried();
    void setLoginTriedAndStartActivity(@NonNull String nextRequest);

    boolean isLoginTriedAndBackOnResume();
    String updateNextRequestAfterOAuthOnResume();

    String getLoginToken();
    void resetOAuth();

}
