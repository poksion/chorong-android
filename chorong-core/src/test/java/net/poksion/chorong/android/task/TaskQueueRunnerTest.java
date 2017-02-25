package net.poksion.chorong.android.task;

import android.os.HandlerThread;
import android.os.Looper;

import junit.framework.Assert;

import net.poksion.chorong.android.task.internal.TaskQueue;
import net.poksion.chorong.android.task.internal.TaskQueueAsyncImpl;
import net.poksion.chorong.android.task.internal.TaskQueueRunner;
import net.poksion.chorong.android.task.internal.TaskQueueSimpleThreadImpl;
import net.poksion.chorong.android.task.internal.TaskQueueSyncImpl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLooper;

import java.lang.ref.WeakReference;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE)
public class TaskQueueRunnerTest {

    private enum QueueType {
        SYNC,
        SIMPLE_THREAD,
        HANDLER_THREAD
    }

    private static class TaskRunnerImpl extends TaskQueueRunner<TaskRunnerOwner> {

        TaskRunnerImpl(TaskQueue<TaskRunnerOwner> taskQueue) {
            super(taskQueue);
        }

        private TaskQueue<TaskRunnerOwner> getTaskQueue() {
            return taskQueue;
        }
    }

    private static class TaskRunnerOwner {
        final TaskRunnerImpl taskRunner;

        TaskRunnerOwner(QueueType queueType) {
            this(queueType, null);
        }

        TaskRunnerOwner(QueueType queueType, Looper looper) {
            TaskQueue<TaskRunnerOwner> taskQueue = null;
            switch (queueType) {
                case SYNC:
                    taskQueue = new TaskQueueSyncImpl<>(this);
                    break;
                case SIMPLE_THREAD:
                    taskQueue = new TaskQueueSimpleThreadImpl<>(this);
                    break;
                case HANDLER_THREAD:
                    taskQueue = new TaskQueueAsyncImpl<>(this, looper, false);
                    break;
            }
            taskRunner = new TaskRunnerImpl(taskQueue);
        }

        TaskQueueRunner<TaskRunnerOwner> getTaskRunner() {
            return taskRunner;
        }

        TaskQueue getTaskQueue() {
            return taskRunner.getTaskQueue();
        }
    }

    @Test
    public void testSendResultAndIfSendLastResultThenRemoveTask() {
        final TaskRunnerOwner runnerOwner = new TaskRunnerOwner(QueueType.SYNC);
        runnerOwner.getTaskRunner().runTask(new Task<TaskRunnerOwner>() {
            @Override
            public void onWork(ResultSender resultSender) {
                resultSender.sendResult(0, null, false);

                // notify that this is the last result
                resultSender.sendResult(1, null, true);

                // not receive anymore
                resultSender.sendResult(2, null, false);
            }

            @Override
            public void onResult(int resultKey, Object resultValue, WeakReference<TaskRunnerOwner> resultListenerRef) {
                if (resultKey == 0) {
                    assertThat(runnerOwner.getTaskQueue().isEmptyTask()).isFalse();
                }

                if (resultKey == 1) {
                    assertThat(runnerOwner.getTaskQueue().isEmptyTask()).isTrue();
                }

                if (resultKey == 2) {
                    Assert.fail("not possible since passed away on resultKey==1");
                }
            }
        });
    }

    private static class TaskFixture {

        boolean gotResult = false;
        WeakReference<TaskRunnerOwner> resultListenerWeakRef;

        Task<TaskRunnerOwner> getTask() {
            gotResult = false;

            return new Task<TaskRunnerOwner>() {
                @Override
                public void onWork(ResultSender resultSender) {
                    try {
                        Thread.sleep(500);
                        resultSender.sendResult(0, null, true);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onResult(int resultKey, Object resultValue, WeakReference<TaskRunnerOwner> resultListenerRef) {
                    assertThat(resultKey).isEqualTo(0);
                    resultListenerWeakRef = resultListenerRef;

                    gotResult = true;
                }
            };
        }

        void waitUntilGotResult() {
            while(!gotResult) {
                try {
                    Thread.sleep(600);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        void waitUntilGotResultWithShadowLooper(Looper looper) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            ShadowLooper shadowLooper = Shadows.shadowOf(looper);
            shadowLooper.runToEndOfTasks();

            waitUntilGotResult();
        }
    }

    @Test
    public void testRunnerWeakRefOnSimpleThreadImpl() {
        TaskFixture taskFixture = new TaskFixture();

        TaskRunnerOwner runnerOwner = new TaskRunnerOwner(QueueType.SIMPLE_THREAD);
        runnerOwner.getTaskRunner().runTask(taskFixture.getTask());

        // remove reference
        // noinspection UnusedAssignment
        runnerOwner = null;
        System.gc();

        taskFixture.waitUntilGotResult();
        assertThat(taskFixture.resultListenerWeakRef.get()).isNull();
    }

    @Test
    public void testHandlerWeakRefOnAsyncImpl() {
        TaskFixture taskFixture = new TaskFixture();

        HandlerThread handlerThread = new HandlerThread("test");
        handlerThread.start();
        assertThat(handlerThread.getLooper()).isNotNull();

        TaskRunnerOwner runnerOwner = new TaskRunnerOwner(QueueType.HANDLER_THREAD, handlerThread.getLooper());
        runnerOwner.getTaskRunner().runTask(taskFixture.getTask());

        // remove reference
        // noinspection UnusedAssignment
        runnerOwner = null;
        System.gc();

        taskFixture.waitUntilGotResultWithShadowLooper(handlerThread.getLooper());
        assertThat(taskFixture.resultListenerWeakRef.get()).isNull();
    }
}
