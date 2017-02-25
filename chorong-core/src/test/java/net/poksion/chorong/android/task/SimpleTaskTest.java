package net.poksion.chorong.android.task;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SimpleTaskTest {

    private Object dummyListener = new Object();

    private static class TestResult {
        int id;
        String value;
    }

    @Test
    public void shouldReceiveResultAfterWork() {
        TaskRunnerSync<Object> taskHandler = new TaskRunnerSync<>(dummyListener);
        taskHandler.runTask(new SimpleTask<TestResult, Object>() {
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
}
