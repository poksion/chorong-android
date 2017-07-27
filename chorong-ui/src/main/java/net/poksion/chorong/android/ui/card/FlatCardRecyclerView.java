package net.poksion.chorong.android.ui.card;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import net.poksion.chorong.android.ui.R;

public class FlatCardRecyclerView extends RecyclerView {

    private final ItemAdapter adapter;

    public FlatCardRecyclerView(Context context) {
        this(context, null);
    }

    public FlatCardRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlatCardRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setLayoutManager(new LinearLayoutManager(context));
        setBackgroundColor(ContextCompat.getColor(context, R.color.flatCardBlank));

        adapter = new ItemAdapter(this);
        setAdapter(adapter);
    }

    public <V, M> void addItem(ViewModel<V, M> viewModel, ViewBinder<V, M> viewBinder) {
        adapter.addItem(viewModel, viewBinder);
    }

    public void clearAllItems() {
        adapter.clearAllItems();
    }

    public void notifyDataSetChanged() {
        adapter.notifyDataSetChanged();
    }

    public void setCustomItemViewInflater(ItemAdapter.ViewInflater itemViewInflater) {
        adapter.setCustomItemViewInflater(itemViewInflater);
    }

    public ViewModel<FlatCardTitleView, String[]> makeTitleViewModel(@NonNull String title, @Nullable String subTitle) {
        return new ViewModel<>(R.layout.flat_card_title, new String[] { title, subTitle } );
    }

    public ViewBinder<FlatCardTitleView, String[]> makeTitleViewBinder() {
        return new ViewBinder<FlatCardTitleView, String[]>() {
            @Override
            public void onBind(FlatCardTitleView view, String[] model) {
                view.setTitle(model[0], model[1]);
            }
        };
    }

    public ViewModel<FlatCardGeneralContentView, String> makeGeneralContentViewModel(@Nullable String content) {
        return new ViewModel<>(R.layout.flat_card_general_content, content);
    }

    public ViewModel<FlatCardTextView, String> makeTextViewModel(@NonNull String content) {
        return new ViewModel<>(R.layout.flat_card_text, content);
    }

    public ViewModel<FlatCardLoadingView, FlatCardLoadingView.LoadingState> makeLoadingViewModel() {
        return new ViewModel<>(R.layout.flat_card_loading, FlatCardLoadingView.LoadingState.START);
    }
}
