package net.poksion.chorong.android.task;

import android.os.Looper;
import net.poksion.chorong.android.task.internal.TaskQueueWithAsync;

public class TaskRunnerAsyncShared<T_Listener> extends TaskQueueRunner<T_Listener> {
    public TaskRunnerAsyncShared(T_Listener listener) {
        super(new TaskQueueWithAsync<>(listener, Looper.getMainLooper(), TaskQueueWithAsync.ThreadType.USE_BACKGROUND_EXECUTOR));
    }
}
