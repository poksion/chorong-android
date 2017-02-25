package net.poksion.chorong.android.task;

import net.poksion.chorong.android.task.internal.TaskQueueAsyncImpl;
import net.poksion.chorong.android.task.internal.TaskQueueRunner;

public class TaskRunnerAsyncShared<T_Listener> extends TaskQueueRunner<T_Listener> {
    public TaskRunnerAsyncShared(T_Listener listener) {
        super(new TaskQueueAsyncImpl<>(listener));
    }
}
