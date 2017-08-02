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

public abstract class TaskQueueImpl<ListenerT> implements TaskQueue<ListenerT> {

    protected abstract void onRun(Task<ListenerT> task, Task.ResultSender taskResultSender);

    private final Map<Long, Task<ListenerT>> taskList = new ConcurrentHashMap<>();
    private final WeakReference<ListenerT> taskResultListenerRef;

    // weak reference store observer holder
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private final List<StoreObserver<?>> storeObserverHolder = new LinkedList<>();

    protected TaskQueueImpl(ListenerT listener) {
        taskResultListenerRef = new WeakReference<>(listener);
    }

    @Override
    public void enqueue(long taskId, Task<ListenerT> task) {
        taskList.put(taskId, task);
        onRun(task, getResultSender(taskId));
    }

    @Override
    public void execute(BlockingTask<ListenerT> task) {
        ListenerT listener = taskResultListenerRef.get();
        if (listener == null) {
            return;
        }
        task.onWork(listener);
    }

    @Override
    public <ResultT> void register(final ObservingTask<ResultT, ListenerT> observingTask) {
        final ObjectStore objectStore = observingTask.getStore();
        final String storeKey = observingTask.getStoreKey();

        StoreObserver<ResultT> storeObserver = getStoreObserver(observingTask);

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
        Task<ListenerT> task;
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

    protected <ResultT> StoreObserver<ResultT> getStoreObserver(final ObservingTask<ResultT, ListenerT> observingTask) {
        return new StoreObserver<ResultT>() {
            @Override
            protected void onChanged(ResultT result) {
                handleObservingTask(observingTask, this, result);
            }
        };
    }

    protected  <ResultT> void handleObservingTask(ObservingTask<ResultT, ListenerT> observingTask, StoreObserver<ResultT> storeObserver, ResultT result) {
        final ObjectStore objectStore = observingTask.getStore();
        final String storeKey = observingTask.getStoreKey();

        ListenerT listener = taskResultListenerRef.get();
        boolean available = (listener != null && observingTask.isAvailable(listener));

        if (available) {
            observingTask.onChanged(result, listener);
        } else {
            objectStore.removeWeakObserver(storeKey, storeObserver);
        }
    }
}
