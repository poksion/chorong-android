package net.poksion.chorong.android.task;

public interface BlockingTask<ListenerT> {
    void onWork(ListenerT listener);
}
