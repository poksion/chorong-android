package net.poksion.chorong.android.task.internal;

import net.poksion.chorong.android.task.Task;

public class TaskQueueWithSync<ListenerT> extends TaskQueueImpl<ListenerT> {

    public TaskQueueWithSync(ListenerT listener) {
        super(listener);
    }

    @Override
    public void onRun(Task<ListenerT> task, Task.ResultSender taskResultSender) {
        task.onWork(taskResultSender);
    }
}
