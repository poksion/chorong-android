package net.poksion.chorong.android.task;

import net.poksion.chorong.android.task.internal.TaskQueueWithSync;

public class TaskRunnerSync<ListenerT> extends TaskQueueRunner<ListenerT> {
    public TaskRunnerSync(ListenerT listener) {
        super(new TaskQueueWithSync<>(listener));
    }
}
