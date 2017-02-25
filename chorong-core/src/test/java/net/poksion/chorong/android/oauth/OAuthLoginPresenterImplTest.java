package net.poksion.chorong.android.oauth;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OAuthLoginPresenterImplTest {

    private OAuthLoginPresenterImpl oAuthLoginPresenter;

    @Mock private LoginTokenManager loginTokenManager;

    @Before
    public void setUp() {
        oAuthLoginPresenter = new OAuthLoginPresenterImpl(loginTokenManager) {
            @Override
            void startLoginActivity() {}
        };
    }

    @Test
    public void testTokenValidationAndLoginTriedAfterInit() {
        when(loginTokenManager.getSavedLoginToken()).thenReturn("");
        assertThat(oAuthLoginPresenter.isEmptyTokenButNotLoginTried()).isTrue();
    }

    @Test
    public void prevLoginTriedIsTrueWhenSetTried() {
        assertThat(oAuthLoginPresenter.isLoginTriedAndBackOnResume()).isFalse();
        oAuthLoginPresenter.setLoginTriedAndStartActivity("");
        assertThat(oAuthLoginPresenter.isLoginTriedAndBackOnResume()).isTrue();
    }

    @Test
    public void whenSuccessUpdateTokenThenUpdateNextRequestOnResume() {
        String fakeToken = "some-token-value";
        when(loginTokenManager.getSavedLoginToken()).thenReturn(fakeToken);

        // start oauth
        String nextRequest = "next-request";
        oAuthLoginPresenter.setLoginTriedAndStartActivity(nextRequest);

        // check on resume
        assertThat(oAuthLoginPresenter.isLoginTriedAndBackOnResume()).isTrue();
        String result = oAuthLoginPresenter.updateNextRequestAfterOAuthOnResume();
        assertThat(result).isEqualTo(nextRequest);

        // if double updateNextRequest, then next should be null
        result = oAuthLoginPresenter.updateNextRequestAfterOAuthOnResume();
        assertThat(result).isNull();

        // after check
        assertThat(oAuthLoginPresenter.isLoginTriedAndBackOnResume()).isFalse();
        assertThat(oAuthLoginPresenter.isEmptyTokenButNotLoginTried()).isFalse();
        assertThat(oAuthLoginPresenter.getLoginToken()).isEqualTo(fakeToken);
    }
}
