package net.poksion.chorong.android.ui.card;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;
import net.poksion.chorong.android.ui.R;

public class FlatCardTextView extends RelativeLayout {

    private final TextView textContentView;

    public FlatCardTextView(Context context) {
        this(context, null);
    }

    public FlatCardTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlatCardTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        inflate(context, R.layout.flat_card_general_content_internal, this);

        findViewById(R.id.card_general_box_content).setVisibility(GONE);
        textContentView = (TextView) findViewById(R.id.card_general_text_content);
    }

    public void setTextContent(@Nullable String content, boolean html) {
        if (content == null) {
            textContentView.setVisibility(GONE);
        } else {
            textContentView.setVisibility(VISIBLE);

            if (html) {
                //noinspection deprecation
                textContentView.setText(Html.fromHtml(content));
            } else {
                textContentView.setText(content);
            }
        }
    }
}
