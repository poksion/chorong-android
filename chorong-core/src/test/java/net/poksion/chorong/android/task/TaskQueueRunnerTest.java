package net.poksion.chorong.android.task;

import android.os.HandlerThread;
import android.os.Looper;

import junit.framework.Assert;

import net.poksion.chorong.android.task.internal.TaskQueueWithAsync;
import net.poksion.chorong.android.task.internal.TaskQueueWithSimpleThread;
import net.poksion.chorong.android.task.internal.TaskQueueWithSync;

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

    private static class RunnerOwnerAndListener {
        private final TaskRunner<RunnerOwnerAndListener> taskRunner;
        private final TaskQueue<RunnerOwnerAndListener> taskQueue;

        RunnerOwnerAndListener(QueueType queueType) {
            this(queueType, null);
        }

        RunnerOwnerAndListener(QueueType queueType, Looper looper) {
            switch (queueType) {
                case SYNC:
                    taskQueue = new TaskQueueWithSync<>(this);
                    break;
                case SIMPLE_THREAD:
                    taskQueue = new TaskQueueWithSimpleThread<>(this);
                    break;
                case HANDLER_THREAD:
                    taskQueue = new TaskQueueWithAsync<>(this, looper, false);
                    break;
                default:
                    taskQueue = null;
            }
            taskRunner = new TaskQueueRunner<>(taskQueue);
        }

        TaskRunner<RunnerOwnerAndListener> getTaskRunner() {
            return taskRunner;
        }

        TaskQueue<RunnerOwnerAndListener> getTaskQueue() {
            return taskQueue;
        }
    }

    @Test
    public void task_should_be_removed_if_send_last_result() {
        final RunnerOwnerAndListener runnerOwnerAndListener = new RunnerOwnerAndListener(QueueType.SYNC);
        runnerOwnerAndListener.getTaskRunner().runTask(new Task<RunnerOwnerAndListener>() {
            @Override
            public void onWork(ResultSender resultSender) {
                resultSender.sendResult(0, null, false);

                // notify that this is the last result
                resultSender.sendResult(1, null, true);

                // not receive anymore
                resultSender.sendResult(2, null, false);
            }

            @Override
            public void onResult(int resultKey, Object resultValue, WeakReference<RunnerOwnerAndListener> resultListenerRef) {
                if (resultKey == 0) {
                    assertThat(runnerOwnerAndListener.getTaskQueue().size()).isEqualTo(1);
                }

                if (resultKey == 1) {
                    assertThat(runnerOwnerAndListener.getTaskQueue().size()).isEqualTo(0);
                }

                if (resultKey == 2) {
                    Assert.fail("not possible since passed away on resultKey==1");
                }
            }
        });
    }

    private static class TaskFixture {

        boolean gotResult = false;
        WeakReference<RunnerOwnerAndListener> resultListenerWeakRef;

        Task<RunnerOwnerAndListener> getTask() {
            gotResult = false;

            return new Task<RunnerOwnerAndListener>() {
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
                public void onResult(int resultKey, Object resultValue, WeakReference<RunnerOwnerAndListener> resultListenerRef) {
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
    public void listener_should_be_managed_as_weak_ref_on_simple_thread() {
        TaskFixture taskFixture = new TaskFixture();

        RunnerOwnerAndListener runnerOwnerAndListener = new RunnerOwnerAndListener(QueueType.SIMPLE_THREAD);
        runnerOwnerAndListener.getTaskRunner().runTask(taskFixture.getTask());

        // remove reference
        // noinspection UnusedAssignment
        runnerOwnerAndListener = null;
        System.gc();

        taskFixture.waitUntilGotResult();
        assertThat(taskFixture.resultListenerWeakRef.get()).isNull();
    }

    @Test
    public void listener_should_be_managed_as_weak_ref_on_shared_handler_thread() {
        TaskFixture taskFixture = new TaskFixture();

        HandlerThread handlerThread = new HandlerThread("test");
        handlerThread.start();
        assertThat(handlerThread.getLooper()).isNotNull();

        RunnerOwnerAndListener runnerOwnerAndListener = new RunnerOwnerAndListener(QueueType.HANDLER_THREAD, handlerThread.getLooper());
        runnerOwnerAndListener.getTaskRunner().runTask(taskFixture.getTask());

        // remove reference
        // noinspection UnusedAssignment
        runnerOwnerAndListener = null;
        System.gc();

        taskFixture.waitUntilGotResultWithShadowLooper(handlerThread.getLooper());
        assertThat(taskFixture.resultListenerWeakRef.get()).isNull();
    }
}
