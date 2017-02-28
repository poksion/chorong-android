package net.poksion.chorong.android.task;

import net.poksion.chorong.android.task.internal.TaskQueueWithSimpleThread;

public class TaskRunnerSimpleThread<T_Listener> extends TaskQueueRunner<T_Listener> {
    public TaskRunnerSimpleThread(T_Listener listener) {
        super(new TaskQueueWithSimpleThread<>(listener));
    }
}
