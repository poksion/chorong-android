package net.poksion.chorong.android.task;

import android.os.Looper;
import net.poksion.chorong.android.task.internal.TaskQueueWithAsync;

public class TaskRunnerAsyncExecutor<ListenerT> extends TaskQueueRunner<ListenerT> {
    public TaskRunnerAsyncExecutor(ListenerT listener) {
        super(new TaskQueueWithAsync<>(listener, Looper.getMainLooper(), TaskQueueWithAsync.ThreadType.USE_EXECUTOR));
    }
}
