package net.poksion.chorong.android.task;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class SimpleTaskTest {

    private final Object dummyListener = new Object();

    private static class TestResult {
        int id;
        String value;
    }

    @Test
    public void result_should_be_received_after_work() {
        TaskRunnerSync<Object> taskRunner = new TaskRunnerSync<>(dummyListener);
        taskRunner.runTask(new SimpleTask<TestResult, Object>() {
            @Override
            protected TestResult onWorkSimple() {
                TestResult result = new TestResult();
                result.id = 1000;
                result.value = "1000";
                return result;
            }

            @Override
            protected void onResultSimple(TestResult testResult, Object dummyListener) {
                assertThat(testResult.id).isEqualTo(1000);
                assertThat(testResult.value).isEqualTo("1000");
            }
        });
    }

    @Test
    public void listener_could_be_null_after_gc_then_should_not_receive_result() {
        Object killedListener = new Object();
        TaskRunnerSync<Object> taskRunner = new TaskRunnerSync<>(killedListener);

        //noinspection UnusedAssignment
        killedListener = null;
        System.gc();

        taskRunner.runTask(new SimpleTask<TestResult, Object>() {
            @Override
            protected TestResult onWorkSimple() {
                TestResult result = new TestResult();
                result.id = 1000;
                result.value = "1000";
                return result;
            }

            @Override
            protected void onResultSimple(TestResult testResult, Object killedListener) {
                fail("listener killed");
            }
        });

    }
}
