package net.poksion.chorong.android.task.internal;

import net.poksion.chorong.android.task.Task;

public class TaskQueueWithSimpleThread<T_Listener> extends TaskQueue<T_Listener> {

    public TaskQueueWithSimpleThread(T_Listener listener) {
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
