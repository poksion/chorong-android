package net.poksion.chorong.android.task.internal;

import net.poksion.chorong.android.task.Task;

public class TaskQueueWithSimpleThread<ListenerT> extends TaskQueueImpl<ListenerT> {

    public TaskQueueWithSimpleThread(ListenerT listener) {
        super(listener);
    }

    @Override
    public void onRun(final Task<ListenerT> task, final Task.ResultSender taskResultSender) {
        new Thread() {
            @Override
            public void run() {
                task.onWork(taskResultSender);
            }
        }.start();
    }
}
