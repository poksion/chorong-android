package net.poksion.chorong.android.task;

import android.os.Looper;
import net.poksion.chorong.android.task.internal.TaskQueueWithAsync;

public class TaskRunnerAsyncShared<ListenerT> extends TaskQueueRunner<ListenerT> {
    public TaskRunnerAsyncShared(ListenerT listener) {
        super(new TaskQueueWithAsync<>(listener, Looper.getMainLooper(), TaskQueueWithAsync.ThreadType.USE_BACKGROUND_EXECUTOR));
    }
}
