package net.poksion.chorong.android.ui.card;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import net.poksion.chorong.android.ui.R;

public class FlatTitleView extends RelativeLayout {

    private final View blankView;
    private final TextView titleTextView;
    private final TextView subTitleTextView;

    public FlatTitleView(Context context) {
        this(context, null);
    }

    public FlatTitleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlatTitleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.flat_card_title_internal, this);

        blankView = findViewById(R.id.card_title_blank);
        titleTextView = (TextView) findViewById(R.id.card_title_text);
        subTitleTextView = (TextView) findViewById(R.id.card_sub_title_text);
    }

    public void setTitle(String title, @Nullable String subTitle) {
        titleTextView.setText(title);

        if (subTitle == null || subTitle.length() == 0) {
            subTitleTextView.setVisibility(GONE);
        } else {
            subTitleTextView.setText(subTitle);
        }
    }

    public void hideBlank() {
        blankView.setVisibility(GONE);
    }

    public void showBlank() {
        blankView.setVisibility(VISIBLE);
    }
}
