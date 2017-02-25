package net.poksion.chorong.android.task.internal;

import net.poksion.chorong.android.task.Task;

public class TaskQueueSimpleThreadImpl<T_Listener> extends TaskQueueSimpleResultHandle<T_Listener> {

    public TaskQueueSimpleThreadImpl(T_Listener listener) {
        super(listener);
    }

    @Override
    public void onRun(final Task<T_Listener> task, final Task.ResultSender taskResultSender) {
        new Thread() {
            @Override
            public void run() {
                task.onWork(taskResultSender);
            }
        }.start();
    }
}
