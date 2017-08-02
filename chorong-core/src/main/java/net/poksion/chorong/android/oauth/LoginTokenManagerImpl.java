package net.poksion.chorong.android.oauth;

import android.content.Context;
import android.content.SharedPreferences;

public class LoginTokenManagerImpl implements LoginTokenManager {
    private final Context applicationContext;

    public LoginTokenManagerImpl(Context context) {
        applicationContext = context.getApplicationContext();
    }

    @Override
    public String getSavedLoginToken() {
        return getSharedPref().getString("token", "");
    }

    @Override
    public boolean saveLoginToken(String token) {
        SharedPreferences.Editor editor = getSharedPref().edit();
        editor.putString("token", token);
        return editor.commit();
    }

    private SharedPreferences getSharedPref() {
        return applicationContext.getSharedPreferences("oauth-login", Context.MODE_PRIVATE);
    }
}
