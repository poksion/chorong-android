package net.poksion.chorong.android.oauth;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import net.poksion.chorong.android.ui.R;

public class LoginActivity extends Activity {

    private static final String REQUEST_URL = "request-url";
    private static final String CALLBACK_SCHEME = "callback-scheme";
    private static final String SUCCESS_HOST = "success-host";
    private static final String TOKEN_QUERY = "token-query";

    public static Intent getIntent(Activity owner, String requestUrl, String callbackScheme, String successHost, String tokenQuery) {
        Intent i = new Intent(owner, LoginActivity.class);

        i.putExtra(REQUEST_URL, requestUrl);
        i.putExtra(CALLBACK_SCHEME, callbackScheme);
        i.putExtra(SUCCESS_HOST, successHost);
        i.putExtra(TOKEN_QUERY, tokenQuery);

        return i;
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Intent i = getIntent();

        final String requestUrl = i.getStringExtra(REQUEST_URL);
        final String callbackScheme = i.getStringExtra(CALLBACK_SCHEME);
        final String successHost = i.getStringExtra(SUCCESS_HOST);
        final String tokenQuery = i.getStringExtra(TOKEN_QUERY);

        final LoginTokenManager loginTokenManager = new LoginTokenManagerImpl(this);

        final WebView webView = (WebView) findViewById(R.id.login_webview);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient() {

            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                Uri requested = Uri.parse(url);
                String scheme = requested.getScheme();

                if (callbackScheme.equals(scheme)) {
                    if(requested.getHost().contains(successHost)){
                        String token = requested.getQueryParameter(tokenQuery);
                        loginTokenManager.saveLoginToken(token);
                    }
                    finish();
                    return true;
                } else {
                    return super.shouldOverrideUrlLoading(view, url);
                }
            }
        });

        // initial loading
        webView.loadUrl(requestUrl);

        // remove background for performance
        getWindow().setBackgroundDrawable(null);
    }
}
