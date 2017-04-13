package net.poksion.chorong.android.ui.card;

import android.support.annotation.LayoutRes;
import android.view.View;

public final class ViewModel<V, M> extends ViewUpdater {

    private M model;
    private ViewBinder<V, M> viewBinder;

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

    public void unbind() {
        if (viewUpdatableAdapter != null) {
            viewUpdatableAdapter.insertOrRemoveItem(modelIdx, false, null, null);
        }

        viewBinder = null;
        viewUpdatableAdapter = null;
        modelIdx = -1;
    }

    public <V2, M2> void concat(ViewModel<V2, M2> viewModel, ViewBinder<V2, M2> viewBinder) {
        insert(modelIdx, viewModel, viewBinder);
    }

    public <V2, M2> void concatInverse(ViewModel<V2, M2> viewModel, ViewBinder<V2, M2> viewBinder) {
        insert(modelIdx-1, viewModel, viewBinder);
    }

    private <V2, M2> void insert(int idx, ViewModel<V2, M2> viewModel, ViewBinder<V2, M2> viewBinder) {
        if (viewUpdatableAdapter != null) {
            viewUpdatableAdapter.insertOrRemoveItem(idx, true, viewModel, viewBinder);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    void onUpdateView(View view) {
        if (viewBinder != null) {
            viewBinder.onBind((V)view, model);
        }
    }

    @Override
    void updateModelIndex(int modelIdx) {
        this.modelIdx = modelIdx;
    }

    void setAdapterInformation(ViewBinder<V, M> viewBinder, ViewUpdatableAdapter viewUpdatableAdapter, int modelIdx) {
        this.viewBinder = viewBinder;
        this.viewUpdatableAdapter = viewUpdatableAdapter;
        this.modelIdx = modelIdx;
    }
}
