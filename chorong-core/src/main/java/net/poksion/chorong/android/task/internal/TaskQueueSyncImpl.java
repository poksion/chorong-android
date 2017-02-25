package net.poksion.chorong.android.task.internal;

import net.poksion.chorong.android.task.Task;

public class TaskQueueSyncImpl<T_Listener> extends TaskQueueSimpleResultHandle<T_Listener> {

    public TaskQueueSyncImpl(T_Listener listener) {
        super(listener);
    }

    @Override
    public void onRun(Task<T_Listener> task, Task.ResultSender taskResultSender) {
        task.onWork(taskResultSender);
    }
}
