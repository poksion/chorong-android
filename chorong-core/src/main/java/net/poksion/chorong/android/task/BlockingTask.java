package net.poksion.chorong.android.task;

public interface BlockingTask<T_Listener> {
    void onWork(T_Listener listener);
}
