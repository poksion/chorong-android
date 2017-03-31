package net.poksion.chorong.android.ui.card;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import net.poksion.chorong.android.ui.R;

public class FlatCardGeneralContentView extends RelativeLayout {

    private final TextView textContentView;
    private final FrameLayout boxContentContainer;

    public FlatCardGeneralContentView(Context context) {
        this(context, null);
    }

    public FlatCardGeneralContentView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlatCardGeneralContentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        inflate(context, R.layout.flat_card_general_content_internal, this);

        textContentView = (TextView) findViewById(R.id.card_general_text_content);
        boxContentContainer = (FrameLayout) findViewById(R.id.card_general_box_content);
    }

    public void setTextContent(@Nullable String content) {
        if (content == null) {
            textContentView.setVisibility(GONE);
        } else {
            textContentView.setVisibility(VISIBLE);
            textContentView.setText(content);
        }
    }

    public void hideBoxContent() {
        boxContentContainer.setVisibility(GONE);
    }

    public FrameLayout getBoxContentContainer() {
        return boxContentContainer;
    }
}
