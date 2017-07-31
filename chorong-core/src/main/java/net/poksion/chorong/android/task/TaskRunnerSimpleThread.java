package net.poksion.chorong.android.task;

import net.poksion.chorong.android.task.internal.TaskQueueWithSimpleThread;

public class TaskRunnerSimpleThread<ListenerT> extends TaskQueueRunner<ListenerT> {
    public TaskRunnerSimpleThread(ListenerT listener) {
        super(new TaskQueueWithSimpleThread<>(listener));
    }
}
