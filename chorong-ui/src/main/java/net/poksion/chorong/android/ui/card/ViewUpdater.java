package net.poksion.chorong.android.ui.card;

import android.view.View;

abstract class ViewUpdater {
    private final int viewResId;

    ViewUpdater(int viewResId) {
        this.viewResId = viewResId;
    }

    int getViewResId() {
        return viewResId;
    }


    abstract void onUpdateView(View view);
    abstract void updateModelIndex(int modelIdx);

}
