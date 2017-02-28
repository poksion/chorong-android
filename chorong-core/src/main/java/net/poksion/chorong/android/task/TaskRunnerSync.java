package net.poksion.chorong.android.task;

import net.poksion.chorong.android.task.internal.TaskQueueWithSync;

public class TaskRunnerSync<T_Listener> extends TaskQueueRunner<T_Listener> {
    public TaskRunnerSync(T_Listener listener) {
        super(new TaskQueueWithSync<>(listener));
    }
}
