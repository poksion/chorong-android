package net.poksion.chorong.android.ui.card;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import net.poksion.chorong.android.ui.R;

public class FlatCardTitleView extends RelativeLayout {

    private final View blankView;
    private final TextView titleTextView;
    private final TextView subTitleTextView;

    public FlatCardTitleView(Context context) {
        this(context, null);
    }

    public FlatCardTitleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlatCardTitleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.flat_card_title_internal, this);

        blankView = findViewById(R.id.card_title_blank);
        titleTextView = (TextView) findViewById(R.id.card_title_text);
        subTitleTextView = (TextView) findViewById(R.id.card_sub_title_text);
    }

    public void setTitle(@NonNull String title, @Nullable String subTitle) {
        titleTextView.setText(title);

        if (subTitle == null || subTitle.length() == 0) {
            subTitleTextView.setVisibility(GONE);
        } else {
            subTitleTextView.setVisibility(VISIBLE);
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
