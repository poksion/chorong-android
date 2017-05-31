package net.poksion.chorong.android.ui.dialog;

public interface DialogFragmentManager<T extends Enum<T>> {

    interface DelayRunner {
        void runDelayed(Runnable runnable);
    }

    enum State {
        NORMAL,
        WAITING,
        SHOWING
    }

    void notifyOnResume(DialogFragmentHost<T> dialogFragmentHost);
    void notifyOnPause(DialogFragmentHost<T> dialogFragmentHost);

    void requestWaitingState(T dialogType, String id, String title, String message);
    void requestNormalState(String id);
    void requestShowingState(T dialogType, String title, String message);

    void setStatelessDialog(T dialogType);
    void showStatelessDialog(T dialogType, String id);
    void hideStatelessDialog(T dialogType, String id);

}
