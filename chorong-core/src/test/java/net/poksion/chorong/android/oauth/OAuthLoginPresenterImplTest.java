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
            protected void startLoginActivity() {}
        };
    }

    @Test
    public void oauth_presenter_should_know_token_state_when_login_token_mgr_does_not_have_saved_token() {
        when(loginTokenManager.getSavedLoginToken()).thenReturn("");
        assertThat(oAuthLoginPresenter.isEmptyTokenButNotLoginTried()).isTrue();
    }

    @Test
    public void login_tried_should_be_true_after_calling_login_tried_and_started_activity() {
        assertThat(oAuthLoginPresenter.checkOnResumeLoginTriedAndBack()).isFalse();
        oAuthLoginPresenter.setLoginTriedAndStartActivity("");
        assertThat(oAuthLoginPresenter.checkOnResumeLoginTriedAndBack()).isTrue();
    }

    @Test
    public void next_request_should_be_updated_after_login_tried_and_start_activity() {
        String fakeToken = "some-token-value";
        when(loginTokenManager.getSavedLoginToken()).thenReturn(fakeToken);

        // start oauth
        String nextRequest = "next-request";
        oAuthLoginPresenter.setLoginTriedAndStartActivity(nextRequest);

        // check on resume
        assertThat(oAuthLoginPresenter.checkOnResumeLoginTriedAndBack()).isTrue();
        String result = oAuthLoginPresenter.updateOnResumeNextRequest();
        assertThat(result).isEqualTo(nextRequest);

        // if double updateNextRequest, then next should be null
        result = oAuthLoginPresenter.updateOnResumeNextRequest();
        assertThat(result).isNull();
    }
}
