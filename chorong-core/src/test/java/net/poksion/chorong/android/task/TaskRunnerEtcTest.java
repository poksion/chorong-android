package net.poksion.chorong.android.task;

import java.lang.ref.WeakReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE)
public class TaskRunnerEtcTest {

    private static class DummyListener {
        String result;
        void notifyResult(String result) {
            this.result = result;
        }
    }

    private DummyListener dummyListener = new DummyListener();
    private BlockingTask<DummyListener> makeDummyTask(final String result) {
        return new BlockingTask<DummyListener>() {
            @Override
            public void onWork(DummyListener dummyListener) {
                dummyListener.notifyResult(result);
            }
        };
    }

    @Test
    public void test_prepared_task_runner() {
        TaskRunner<DummyListener> taskRunner = new TaskRunnerSimpleThread<>(dummyListener);
        taskRunner.runBlockingTask(makeDummyTask("simple-thread"));
        assertThat(dummyListener.result).isEqualTo("simple-thread");

        taskRunner = new TaskRunnerAsyncShared<>(dummyListener);
        taskRunner.runBlockingTask(makeDummyTask("async-shared"));
        assertThat(dummyListener.result).isEqualTo("async-shared");

        taskRunner = new TaskRunnerSync<>(dummyListener);
        taskRunner.runBlockingTask(makeDummyTask("sync"));
        assertThat(dummyListener.result).isEqualTo("sync");
    }

    @Test
    public void async_shared_should_be_running_with_work_thread() {
        DummyListener dummyListenerForGc = new DummyListener();
        TaskRunner<DummyListener> taskRunner = new TaskRunnerAsyncShared<>(dummyListenerForGc);

        dummyListenerForGc = null;
        System.gc();

        BlockingTask<DummyListener> blockingTask = new BlockingTask<DummyListener>() {
            @Override
            public void onWork(DummyListener dummyListener) {
                fail("since the dummy listener is null, not call here");
            }
        };
        taskRunner.runBlockingTask(blockingTask);

        final Object lock = new Object();

        Task<DummyListener> task = new Task<DummyListener>() {
            @Override
            public void onWork(ResultSender resultSender) {
                String name = Thread.currentThread().getName();
                assertThat(name).contains("TaskQueueWithAsync Worker Thread");

                synchronized(lock) {
                    lock.notify();
                }
            }

            @Override
            public void onResult(int resultKey, Object resultValue, WeakReference<DummyListener> resultListenerRef) {

            }
        };

        taskRunner.runTask(task);

        synchronized(lock) {
            try {
                lock.wait();
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
