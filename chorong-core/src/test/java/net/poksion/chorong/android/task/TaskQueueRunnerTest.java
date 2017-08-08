package net.poksion.chorong.android.task;

import android.os.HandlerThread;
import android.os.Looper;

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

    private static class ListenableTaskApp {
        private final TaskRunner<ListenableTaskApp> taskRunner;
        private final TaskQueue<ListenableTaskApp> taskQueue;

        ListenableTaskApp(QueueType queueType) {
            this(queueType, null);
        }

        ListenableTaskApp(QueueType queueType, Looper looper) {
            switch (queueType) {
                case SYNC:
                    taskQueue = new TaskQueueWithSync<>(this);
                    break;
                case SIMPLE_THREAD:
                    taskQueue = new TaskQueueWithSimpleThread<>(this);
                    break;
                case HANDLER_THREAD:
                    taskQueue = new TaskQueueWithAsync<>(this, looper, TaskQueueWithAsync.ThreadType.USE_BACKGROUND_EXECUTOR);
                    break;
                default:
                    taskQueue = null;
            }
            taskRunner = new TaskQueueRunner<>(taskQueue);
        }

        TaskRunner<ListenableTaskApp> getTaskRunner() {
            return taskRunner;
        }

        TaskQueue<ListenableTaskApp> getTaskQueue() {
            return taskQueue;
        }
    }

    private static class TaskFixture {
        boolean gotResult = false;
        WeakReference<ListenableTaskApp> taskListenerWeakRef;

        Task<ListenableTaskApp> provideTestTask() {
            gotResult = false;

            return new Task<ListenableTaskApp>() {
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
                public void onResult(int resultKey, Object resultValue, WeakReference<ListenableTaskApp> resultListenerRef) {
                    assertThat(resultKey).isEqualTo(0);
                    taskListenerWeakRef = resultListenerRef;

                    gotResult = true;
                }
            };
        }

        void awaitRunningTask() {
            while(!gotResult) {
                try {
                    Thread.sleep(600);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        void awaitRunningTaskWithLooper(Looper looper) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            ShadowLooper shadowLooper = Shadows.shadowOf(looper);
            shadowLooper.runToEndOfTasks();

            awaitRunningTask();
        }
    }

    @Test
    public void task_should_be_kept_unless_last_result() {
        final int DUMMY_RESULT_ID = 7;

        final ListenableTaskApp listenableTaskApp = new ListenableTaskApp(QueueType.SYNC);
        listenableTaskApp.getTaskRunner().runTask(new Task<ListenableTaskApp>() {
            @Override
            public void onWork(ResultSender resultSender) {
                resultSender.sendResult(DUMMY_RESULT_ID, null, false);
            }

            @Override
            public void onResult(int resultKey, Object resultValue, WeakReference<ListenableTaskApp> resultListenerRef) {
                assertThat(resultKey).isEqualTo(DUMMY_RESULT_ID);
                assertThat(listenableTaskApp.getTaskQueue().size()).isEqualTo(1);
            }
        });
    }

    @Test
    public void task_should_be_removed_if_send_last_result() {
        final int DUMMY_RESULT_ID = 7;

        final ListenableTaskApp listenableTaskApp = new ListenableTaskApp(QueueType.SYNC);
        listenableTaskApp.getTaskRunner().runTask(new Task<ListenableTaskApp>() {
            @Override
            public void onWork(ResultSender resultSender) {
                // notify that this is the last result
                resultSender.sendResult(DUMMY_RESULT_ID, null, true);
            }

            @Override
            public void onResult(int resultKey, Object resultValue, WeakReference<ListenableTaskApp> resultListenerRef) {
                assertThat(resultKey).isEqualTo(DUMMY_RESULT_ID);
                assertThat(listenableTaskApp.getTaskQueue().size()).isEqualTo(0);
            }
        });
    }

    @Test
    public void result_should_not_receive_if_last_result_sent() {
        final int DUMMY_RESULT_ID_1 = 7;
        final int DUMMY_RESULT_ID_2 = 3;
        final int[] onResultCount = new int[] {0};

        final ListenableTaskApp listenableTaskApp = new ListenableTaskApp(QueueType.SYNC);
        listenableTaskApp.getTaskRunner().runTask(new Task<ListenableTaskApp>() {
            @Override
            public void onWork(ResultSender resultSender) {
                // notify that this is the last result
                resultSender.sendResult(DUMMY_RESULT_ID_1, null, true);

                // never receive this result
                resultSender.sendResult(DUMMY_RESULT_ID_2, null, false);
            }

            @Override
            public void onResult(int resultKey, Object resultValue, WeakReference<ListenableTaskApp> resultListenerRef) {
                assertThat(resultKey).isEqualTo(DUMMY_RESULT_ID_1);
                assertThat(listenableTaskApp.getTaskQueue().size()).isEqualTo(0);
                onResultCount[0] += 1;
            }
        });

        assertThat(onResultCount[0]).isEqualTo(1);
    }

    @Test
    public void listener_should_be_managed_as_weak_ref_on_simple_thread() {
        TaskFixture taskFixture = new TaskFixture();

        ListenableTaskApp listenableTaskApp = new ListenableTaskApp(QueueType.SIMPLE_THREAD);
        listenableTaskApp.getTaskRunner().runTask(taskFixture.provideTestTask());

        // remove reference
        // noinspection UnusedAssignment
        listenableTaskApp = null;
        System.gc();

        taskFixture.awaitRunningTask();
        assertThat(taskFixture.taskListenerWeakRef.get()).isNull();
    }

    @Test
    public void listener_should_be_managed_as_weak_ref_on_shared_handler_thread() {
        TaskFixture taskFixture = new TaskFixture();

        HandlerThread handlerThread = new HandlerThread("test");
        handlerThread.start();
        assertThat(handlerThread.getLooper()).isNotNull();

        ListenableTaskApp listenableTaskApp = new ListenableTaskApp(QueueType.HANDLER_THREAD, handlerThread.getLooper());
        listenableTaskApp.getTaskRunner().runTask(taskFixture.provideTestTask());

        // remove reference
        // noinspection UnusedAssignment
        listenableTaskApp = null;
        System.gc();

        taskFixture.awaitRunningTaskWithLooper(handlerThread.getLooper());
        assertThat(taskFixture.taskListenerWeakRef.get()).isNull();
    }
}
