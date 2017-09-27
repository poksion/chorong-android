package net.poksion.chorong.android.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class ObservingBufferTest {

    private static class StubResult implements ObservingBuffer.Unique {

        private final String id;

        StubResult(String id) {
            this.id = id;
        }

        @Override
        public String getId() {
            return id;
        }
    }

    private String[] dummyListener;

    @Before
    public void setUp() {
        dummyListener = new String[] { "dummy-listener" };
    }

    @Test
    public void buffering_should_store_before_completing_main_task() {
        List<StubResult> onMainResults = makeDummyBuffer(3, 2);
        List<StubResult> onSubResults = makeDummyBuffer(3, 0);

        ObservingBuffer<StubResult, String[]> observingBuffer = new ObservingBuffer<>(
                new ObservingBuffer.Callback<StubResult, String[]>() {
                    @Override
                    public void completeOnMain(List<StubResult> results, String[] listener) {
                        listener[0] = "called";

                        // "2" is overlapped : 0, 1, 2 and 2, 3, 4
                        assertThat(results.size()).isEqualTo(5);
                    }

                    @Override
                    public void completeOnSub(List<StubResult> results, String[] listener) {
                        fail("never complete on sub (just listening sub task)");
                    }
                }
        );

        observingBuffer.listenSub(onSubResults, dummyListener);
        observingBuffer.completeMain(onMainResults, dummyListener);

        assertThat(dummyListener[0]).isEqualTo("called");
    }

    @Test
    public void buffering_should_call_complete_directly_after_completing_main_task() {
        ObservingBuffer<StubResult, String[]> observingBuffer = new ObservingBuffer<>(
                new ObservingBuffer.Callback<StubResult, String[]>() {
                    @Override
                    public void completeOnMain(List<StubResult> results, String[] listener) {
                        listener[0] = "called";
                    }

                    @Override
                    public void completeOnSub(List<StubResult> results, String[] listener) {
                        listener[0] = "called-on-listenSub";
                        assertThat(results.size()).isEqualTo(3);
                    }
                }
        );

        observingBuffer.completeMain(new ArrayList<StubResult>(), dummyListener);
        assertThat(dummyListener[0]).isEqualTo("called");

        List<StubResult> afterCompletingMainTaskResults = makeDummyBuffer(3, 0);
        observingBuffer.listenSub(afterCompletingMainTaskResults, dummyListener);
        assertThat(dummyListener[0]).isEqualTo("called-on-listenSub");

        // if re-start main task, then not calling completeOnSub on listenSub
        observingBuffer.startMain();
        List<StubResult> afterReStartMainTaskResult = makeDummyBuffer(4, 0);
        dummyListener[0] = "restart-main-task";
        observingBuffer.listenSub(afterReStartMainTaskResult, dummyListener);
        assertThat(dummyListener[0]).isEqualTo("restart-main-task");
    }

    private List<StubResult> makeDummyBuffer(int cnt, int offset) {
        List<StubResult> results = new ArrayList<>();
        for (int i = 0; i < cnt; ++i) {
            results.add(new StubResult(Integer.toString(i + offset)));
        }
        return results;
    }

}
