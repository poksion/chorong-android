package net.poksion.chorong.android.task;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Assertions.assertThat;

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
}
