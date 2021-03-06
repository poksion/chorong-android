package net.poksion.chorong.android.task;

import java.lang.ref.WeakReference;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
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

    private final DummyListener dummyListener = new DummyListener();
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

        taskRunner = new TaskRunnerAsyncDedicated<>(dummyListener);
        taskRunner.runBlockingTask(makeDummyTask("async-dedicated"));
        assertThat(dummyListener.result).isEqualTo("async-dedicated");

        taskRunner = new TaskRunnerAsyncExecutor<>(dummyListener);
        taskRunner.runBlockingTask(makeDummyTask("async-executor"));
        assertThat(dummyListener.result).isEqualTo("async-executor");

        taskRunner = new TaskRunnerSync<>(dummyListener);
        taskRunner.runBlockingTask(makeDummyTask("sync"));
        assertThat(dummyListener.result).isEqualTo("sync");
    }

    @SuppressWarnings("UnusedAssignment")
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

        @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
        final BlockingQueue<Boolean> lock = new LinkedBlockingQueue<>();

        Task<DummyListener> task = new Task<DummyListener>() {
            @Override
            public void onWork(ResultSender resultSender) {
                String name = Thread.currentThread().getName();
                assertThat(name).contains("TaskQueueWithAsync Worker Thread");

                try {
                    lock.put(true);
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResult(int resultKey, Object resultValue, WeakReference<DummyListener> resultListenerRef) {

            }
        };

        taskRunner.runTask(task);

        try {
            lock.poll(10, TimeUnit.SECONDS);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }
}
