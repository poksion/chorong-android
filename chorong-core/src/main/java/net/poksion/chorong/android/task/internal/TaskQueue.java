package net.poksion.chorong.android.task.internal;

import net.poksion.chorong.android.task.BlockingTask;
import net.poksion.chorong.android.task.Task;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class TaskQueue<T_Listener> {

    abstract Task.ResultSender getResultSender(long taskId);
    abstract void onRun(Task<T_Listener> task, Task.ResultSender taskResultSender);

    private final Map<Long, Task<T_Listener>> taskList = new ConcurrentHashMap<>();
    private final WeakReference<T_Listener> taskResultListenerRef;

    TaskQueue(T_Listener listener) {
        taskResultListenerRef = new WeakReference<>(listener);
    }

    void handleResult(int resultId, Object resultValue, boolean lastResult, long taskId) {
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

    public void run(Task<T_Listener> task) {
        long taskId = System.currentTimeMillis();
        taskList.put(taskId, task);

        onRun(task, getResultSender(taskId));
    }

    public void run(BlockingTask<T_Listener> task) {
        T_Listener listener = taskResultListenerRef.get();
        if (listener == null) {
            return;
        }
        task.onWork(listener);
    }

    public boolean isEmptyTask() {
        return taskList.isEmpty();
    }
}
