package net.poksion.chorong.android.ui.card;

import android.support.annotation.LayoutRes;
import android.view.View;

public final class ViewModel<V, M> extends ViewUpdater {

    private M model;
    private ViewModelBinder<V, M> viewModelBinder;

    private ViewUpdatableAdapter viewUpdatableAdapter;
    private int modelIdx = -1;

    public ViewModel(@LayoutRes int viewResId, M model) {
        super(viewResId);
        this.model = model;
    }

    public void updateModel(M model) {
        this.model = model;

        if (viewUpdatableAdapter != null && modelIdx >= 0) {
            viewUpdatableAdapter.notifyItemChanged(modelIdx);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    void onUpdateView(View view) {
        if (viewModelBinder != null) {
            viewModelBinder.onBind((V)view, model);
        }
    }

    void setAdapterInformation(ViewModelBinder<V, M> viewModelBinder, ViewUpdatableAdapter viewUpdatableAdapter, int modelIdx) {
        this.viewModelBinder = viewModelBinder;
        this.viewUpdatableAdapter = viewUpdatableAdapter;
        this.modelIdx = modelIdx;
    }
}
