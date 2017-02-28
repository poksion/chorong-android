package net.poksion.chorong.android.task.internal;

import net.poksion.chorong.android.task.Task;

public class TaskQueueWithSync<T_Listener> extends TaskQueue<T_Listener> {

    public TaskQueueWithSync(T_Listener listener) {
        super(listener);
    }

    @Override
    public void onRun(Task<T_Listener> task, Task.ResultSender taskResultSender) {
        task.onWork(taskResultSender);
    }
}
