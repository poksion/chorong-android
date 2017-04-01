package net.poksion.chorong.android.ui.card;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import net.poksion.chorong.android.ui.R;

public class FlatCardLoadingView extends FrameLayout {

    public enum LoadingState {
        START,
        STOP,
        FAIL
    }

    public interface OnRetryListener {
        void onRetry();
    }

    private final TextView loadingFailTextView;
    private final ProgressBar progressView;

    public FlatCardLoadingView(@NonNull Context context) {
        this(context, null);
    }

    public FlatCardLoadingView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlatCardLoadingView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        inflate(context, R.layout.flat_card_loading_internal, this);

        loadingFailTextView = (TextView) findViewById(R.id.loading_fail_text);
        progressView = (ProgressBar) findViewById(R.id.loading_indicator);
    }

    public void startLoading() {
        loadingFailTextView.setVisibility(GONE);
        progressView.setVisibility(VISIBLE);
    }

    public void stopLoading() {
        if (progressView.getVisibility() != GONE) {
            progressView.setVisibility(GONE);
        }
    }

    public void failLoading(@NonNull final String loadingFailText, @Nullable final OnRetryListener retryListener) {
        stopLoading();

        loadingFailTextView.setVisibility(VISIBLE);
        loadingFailTextView.setText(loadingFailText);

        if (retryListener != null) {
            loadingFailTextView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    retryListener.onRetry();
                }
            });
        }
    }
}
