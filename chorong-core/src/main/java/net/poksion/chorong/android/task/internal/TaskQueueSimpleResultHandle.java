package net.poksion.chorong.android.task.internal;

import net.poksion.chorong.android.task.Task;

abstract class TaskQueueSimpleResultHandle<T_Listener> extends TaskQueue<T_Listener> {

    TaskQueueSimpleResultHandle(T_Listener listener) {
        super(listener);
    }

    @Override
    public final Task.ResultSender getResultSender(final long taskId) {
        return new Task.ResultSender() {
            @Override
            public void sendResult(int resultId, Object resultValue, boolean lastResult) {
                handleResult(resultId, resultValue, lastResult, taskId);
            }
        };
    }
}
