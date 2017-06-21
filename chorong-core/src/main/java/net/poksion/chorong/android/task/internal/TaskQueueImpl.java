package net.poksion.chorong.android.task.internal;

import java.util.LinkedList;
import java.util.List;
import net.poksion.chorong.android.store.ObjectStore;
import net.poksion.chorong.android.store.StoreObserver;
import net.poksion.chorong.android.task.BlockingTask;
import net.poksion.chorong.android.task.ObservingTask;
import net.poksion.chorong.android.task.Task;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.poksion.chorong.android.task.TaskQueue;

public abstract class TaskQueueImpl<T_Listener> implements TaskQueue<T_Listener> {

    protected abstract void onRun(Task<T_Listener> task, Task.ResultSender taskResultSender);

    private final Map<Long, Task<T_Listener>> taskList = new ConcurrentHashMap<>();
    private final WeakReference<T_Listener> taskResultListenerRef;

    // weak reference store observer holder
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private final List<StoreObserver<?>> storeObserverHolder = new LinkedList<>();

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
    public <T_Result> void register(final ObservingTask<T_Result, T_Listener> observingTask) {
        final ObjectStore objectStore = observingTask.getStore();
        final String storeKey = observingTask.getStoreKey();

        StoreObserver<T_Result> storeObserver = getStoreObserver(observingTask);

        storeObserverHolder.add(storeObserver);
        objectStore.addWeakObserver(storeKey, storeObserver, false);
    }

    @Override
    public int size() {
        return taskList.size();
    }

    protected Task.ResultSender getResultSender(final long taskId) {
        return new Task.ResultSender() {
            @Override
            public void sendResult(int resultId, Object resultValue, boolean lastResult) {
                handleResult(resultId, resultValue, lastResult, taskId);
            }
        };
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

    protected <T_Result> StoreObserver<T_Result> getStoreObserver(final ObservingTask<T_Result, T_Listener> observingTask) {
        return new StoreObserver<T_Result>() {
            @Override
            protected void onChanged(T_Result result) {
                handleObservingTask(observingTask, this, result);
            }
        };
    };

    protected  <T_Result> void handleObservingTask(ObservingTask<T_Result, T_Listener> observingTask, StoreObserver<T_Result> storeObserver, T_Result result) {
        final ObjectStore objectStore = observingTask.getStore();
        final String storeKey = observingTask.getStoreKey();

        T_Listener listener = taskResultListenerRef.get();
        boolean available = (listener != null && observingTask.isAvailable(listener));

        if (available) {
            observingTask.onChanged(result, listener);
        } else {
            objectStore.removeWeakObserver(storeKey, storeObserver);
        }
    }
}
