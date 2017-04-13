package net.poksion.chorong.android.ui.card;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

class ViewUpdatableAdapter extends RecyclerView.Adapter<ViewUpdatableAdapter.RecycleViewHolder> {

    static class RecycleViewHolder extends RecyclerView.ViewHolder {

        final View view;

        RecycleViewHolder(View itemView) {
            super(itemView);

            this.view = itemView;
        }
    }

    private List<ViewUpdater> viewUpdaterList = new ArrayList<>();
    private final RecyclerView recyclerView;

    ViewUpdatableAdapter(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    @Override
    public RecycleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(recyclerView.getContext());
        View view = layoutInflater.inflate(viewType, parent, false);
        return new RecycleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecycleViewHolder holder, int position) {
        viewUpdaterList.get(position).onUpdateView(holder.view);
    }

    @Override
    public int getItemCount() {
        return viewUpdaterList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return viewUpdaterList.get(position).getViewResId();
    }

    <V, M> void addItem(ViewModel<V, M> viewModel, ViewBinder<V, M> viewBinder) {
        int nextPos = viewUpdaterList.size();

        viewUpdaterList.add(viewModel);
        viewModel.setAdapterInformation(viewBinder, this, nextPos);
    }

    <V, M> void insertOrRemoveItem(int modelIdx, boolean insertMode,
            @Nullable ViewModel<V, M> viewModel,
            @Nullable ViewBinder<V, M> viewBinder) {

        if (modelIdx < 0) {
            if (modelIdx != -1 || !insertMode) {
                throw new IllegalArgumentException("modelIdx : " + modelIdx);
            }
        }

        if (modelIdx >= viewUpdaterList.size() || (insertMode && viewModel == null)) {
            throw new IllegalArgumentException("modelIdx : " + modelIdx + ", modelSize : " + viewUpdaterList.size());
        }

        if (insertMode) {
            // insert next current model index
            modelIdx++;

            if (modelIdx == viewUpdaterList.size()) {
                viewUpdaterList.add(viewModel);
            } else {
                viewUpdaterList.add(modelIdx, viewModel);
            }
            viewModel.setAdapterInformation(viewBinder, this, modelIdx);
        } else {
            viewUpdaterList.remove(modelIdx);
        }

        int newListSize = viewUpdaterList.size();
        for (int i = modelIdx; i < newListSize; ++i) {
            viewUpdaterList.get(i).updateModelIndex(i);
        }

        if (insertMode) {
            notifyItemInserted(modelIdx);
        } else {
            notifyItemRemoved(modelIdx);
        }

        notifyItemRangeChanged(modelIdx, newListSize - modelIdx);
    }

}
