package net.poksion.chorong.android.ui.card;

public interface ViewModelBinder<V, M> {
    void onBind(V view, M model);
}
