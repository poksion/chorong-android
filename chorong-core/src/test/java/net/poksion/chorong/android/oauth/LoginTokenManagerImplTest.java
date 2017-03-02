package net.poksion.chorong.android.oauth;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE)
public class LoginTokenManagerImplTest {
    private LoginTokenManagerImpl loginTokenManager;

    @Before
    public void setUp() {
        loginTokenManager = new LoginTokenManagerImpl(RuntimeEnvironment.application.getApplicationContext());
    }

    @Test
    public void saved_token_should_be_returned() {
        loginTokenManager.saveLoginToken("dummy-token");
        assertThat(loginTokenManager.getSavedLoginToken()).isEqualTo("dummy-token");
    }
}
