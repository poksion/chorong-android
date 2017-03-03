package net.poksion.chorong.android.task.internal;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;

import net.poksion.chorong.android.task.Task;

import java.lang.ref.WeakReference;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskQueueWithAsync<T_Listener> extends TaskQueueImpl<T_Listener> {

    // Thread executor
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE = 1;

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger count = new AtomicInteger(1);

        @SuppressWarnings("NullableProblems")
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "TaskQueueWithAsync Worker Thread #" + count.getAndIncrement());
        }
    };

    private static final BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue<>();
    private static final Executor sThreadPoolExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);

    private static class InternalMessage {
        Object resultValue;
        boolean lastResult;
        long taskId;

        InternalMessage(Object resultValue, boolean lastResult, long taskId) {
            this.resultValue = resultValue;
            this.lastResult = lastResult;
            this.taskId = taskId;
        }
    }

    // internal handler
    private static class InternalHandler<T_Listener> extends Handler {
        private final WeakReference<TaskQueueWithAsync<T_Listener>> ownerRef;

        private InternalHandler(Looper looper, TaskQueueWithAsync<T_Listener> owner) {
            super(looper);

            ownerRef = new WeakReference<>(owner);
        }

        private void sendMessage(int resultId, Object resultValue, boolean lastResult, long taskId) {
            InternalMessage internalMessage = new InternalMessage(resultValue, lastResult, taskId);
            Message msg = Message.obtain(this, resultId, internalMessage);
            sendMessage(msg);
        }

        @Override
        public void handleMessage(Message msg) {
            @SuppressWarnings("unchecked")
            InternalMessage internalMessage = (InternalMessage) msg.obj;

            TaskQueueWithAsync<T_Listener> owner = ownerRef.get();
            if (owner != null) {
                owner.handleResult(msg.what, internalMessage.resultValue, internalMessage.lastResult, internalMessage.taskId);
            }
        }
    }

    private final InternalHandler<T_Listener> internalHandler;
    private final boolean useSharedExecutor;

    public TaskQueueWithAsync(T_Listener listener) {
        this(listener, Looper.getMainLooper(), true);
    }

    public TaskQueueWithAsync(T_Listener listener, Looper looper, boolean useSharedExecutor) {
        super(listener);

        internalHandler = new InternalHandler<>(looper, this);
        this.useSharedExecutor = useSharedExecutor;
    }

    @Override
    protected void onRun(final Task<T_Listener> task, final Task.ResultSender taskResultSender) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (useSharedExecutor) {
                    // down priority to background
                    Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                }

                task.onWork(taskResultSender);
            }
        };

        if (useSharedExecutor) {
            sThreadPoolExecutor.execute(runnable);
        } else {
            new Thread(runnable).start();
        }
    }

    @Override
    protected Task.ResultSender getResultSender(final long taskId) {
        return new Task.ResultSender() {
            @Override
            public void sendResult(int resultId, Object resultValue, boolean lastResult) {
                internalHandler.sendMessage(resultId, resultValue, lastResult, taskId);
            }
        };
    }

}
