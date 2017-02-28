package net.poksion.chorong.android.task.internal;

import net.poksion.chorong.android.task.BlockingTask;
import net.poksion.chorong.android.task.Task;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.poksion.chorong.android.task.TaskQueue;

public abstract class TaskQueueImpl<T_Listener> implements TaskQueue<T_Listener> {

    protected abstract void onRun(Task<T_Listener> task, Task.ResultSender taskResultSender);

    protected Task.ResultSender getResultSender(final long taskId) {
        return new Task.ResultSender() {
            @Override
            public void sendResult(int resultId, Object resultValue, boolean lastResult) {
                handleResult(resultId, resultValue, lastResult, taskId);
            }
        };
    }

    private final Map<Long, Task<T_Listener>> taskList = new ConcurrentHashMap<>();
    private final WeakReference<T_Listener> taskResultListenerRef;

    protected TaskQueueImpl(T_Listener listener) {
        taskResultListenerRef = new WeakReference<>(listener);
    }

    @Override
    public void enqueue(long taskId, Task<T_Listener> task) {
        taskList.put(taskId, task);
        onRun(task, getResultSender(taskId));
    }

    @Override
    public void execute(BlockingTask<T_Listener> task) {
        T_Listener listener = taskResultListenerRef.get();
        if (listener == null) {
            return;
        }
        task.onWork(listener);
    }

    @Override
    public int size() {
        return taskList.size();
    }

    protected void handleResult(int resultId, Object resultValue, boolean lastResult, long taskId) {
        Task<T_Listener> task;
        if (lastResult) {
            task = taskList.remove(taskId);
        } else {
            task = taskList.get(taskId);
        }

        if (task == null) {
            return;
        }

        task.onResult(resultId, resultValue, taskResultListenerRef);
    }
}
