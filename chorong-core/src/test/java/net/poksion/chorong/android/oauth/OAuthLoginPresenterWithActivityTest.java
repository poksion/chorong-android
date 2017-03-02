package net.poksion.chorong.android.oauth;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OAuthLoginPresenterWithActivityTest {

    private OAuthLoginPresenterWithActivity oAuthLoginPresenter;

    @Mock private LoginTokenManager loginTokenManager;

    @Before
    public void setUp() {
        oAuthLoginPresenter = new OAuthLoginPresenterWithActivity(loginTokenManager) {
            @Override
            protected void startLoginActivity() {}
        };
    }

    @Test
    public void oauth_presenter_should_know_token_state_when_login_token_mgr_have_empty_token() {
        when(loginTokenManager.getSavedLoginToken()).thenReturn("");
        assertThat(oAuthLoginPresenter.getOAuthState()).isEqualTo(OAuthLoginPresenter.OAuthState.OAUTH_EMPTY);
    }

    @Test
    public void startOAuth_should_change_state_to_on_resume() {
        OAuthLoginPresenter.OAuthState onResumeState = OAuthLoginPresenter.OAuthState.OAUTH_ON_RESUME;

        assertThat(oAuthLoginPresenter.getOAuthState()).isNotEqualTo(onResumeState);
        oAuthLoginPresenter.startOAuth(OAuthLoginPresenter.OAuthState.OAUTH_EMPTY, "");
        assertThat(oAuthLoginPresenter.getOAuthState()).isEqualTo(onResumeState);
    }

    @Test
    public void next_request_should_be_updated_after_login_tried_and_start_activity() {
        String fakeToken = "some-token-value";
        when(loginTokenManager.getSavedLoginToken()).thenReturn(fakeToken);

        String nextRequest = "next-request";
        oAuthLoginPresenter.startOAuth(OAuthLoginPresenter.OAuthState.OAUTH_EMPTY, nextRequest);

        String result = oAuthLoginPresenter.completeOAuth(OAuthLoginPresenter.OAuthState.OAUTH_ON_RESUME);
        assertThat(result).isEqualTo(nextRequest);

        // if double completed, then result should be null
        result = oAuthLoginPresenter.completeOAuth(OAuthLoginPresenter.OAuthState.OAUTH_ON_RESUME);
        assertThat(result).isNull();
    }

    @Test
    public void reset_should_be_clear_state() {
        oAuthLoginPresenter.startOAuth(OAuthLoginPresenter.OAuthState.OAUTH_EMPTY, "next-request");
        assertThat(oAuthLoginPresenter.getOAuthState()).isEqualTo(OAuthLoginPresenter.OAuthState.OAUTH_ON_RESUME);

        oAuthLoginPresenter.resetOAuth();
        assertThat(oAuthLoginPresenter.getOAuthState()).isEqualTo(OAuthLoginPresenter.OAuthState.OAUTH_EMPTY);
    }

    @Test
    public void start_and_complete_oauth_should_be_called_with_valid_state() {

        boolean caught = false;
        try {
            oAuthLoginPresenter.startOAuth(OAuthLoginPresenter.OAuthState.OAUTH_ON_RESUME, "next-request");
            fail("should be OAUTH_EMPTY");
        } catch(AssertionError e) {
            caught = true;
        }

        assertThat(caught).isTrue();

        caught = false;
        try {
            oAuthLoginPresenter.completeOAuth(OAuthLoginPresenter.OAuthState.OAUTH_EMPTY);
            fail("should be OAUTH_ON_RESUME");
        } catch(AssertionError e) {
            caught = true;
        }

        assertThat(caught).isTrue();

    }
}
