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
    private ObservingBuffer<StubResult, String[]> observingBuffer;

    @Before
    public void setUp() {
        observingBuffer = new ObservingBuffer<>();
        dummyListener = new String[] { "dummy-listener" };
    }

    @Test
    public void buffering_should_store_before_completing_main_task() {
        List<StubResult> onBufferingResults = makeDummyBuffer(3, 0);
        observingBuffer.buffering(onBufferingResults, dummyListener, new ObservingBuffer.Callback<StubResult, String[]>() {
            @Override
            public void onComplete(List<StubResult> results, String[] listener) {
                fail("should not complete before completing main task");
            }
        });

        List<StubResult> onMainTaskResults = makeDummyBuffer(3, 2);
        observingBuffer.completeMainTask(onMainTaskResults, dummyListener, new ObservingBuffer.Callback<StubResult, String[]>() {
            @Override
            public void onComplete(List<StubResult> results, String[] listener) {
                listener[0] = "called";

                // "2" is overlapped : 0, 1, 2 and 2, 3, 4
                assertThat(results.size()).isEqualTo(5);
            }
        });

        assertThat(dummyListener[0]).isEqualTo("called");
    }

    @Test
    public void buffering_should_call_complete_directly_after_completing_main_task() {
        observingBuffer.completeMainTask(new ArrayList<StubResult>(), dummyListener, new ObservingBuffer.Callback<StubResult, String[]>() {
            @Override
            public void onComplete(List<StubResult> results, String[] listener) {
                listener[0] = "called";
            }
        });

        assertThat(dummyListener[0]).isEqualTo("called");

        List<StubResult> afterCompletingMainTaskResults = makeDummyBuffer(3, 0);
        observingBuffer.buffering(afterCompletingMainTaskResults, dummyListener, new ObservingBuffer.Callback<StubResult, String[]>() {
            @Override
            public void onComplete(List<StubResult> results, String[] listener) {
                listener[0] = "called-on-buffering";

                assertThat(results.size()).isEqualTo(3);
            }
        });

        assertThat(dummyListener[0]).isEqualTo("called-on-buffering");

        // if reset main task, then not calling onComplete on buffering
        observingBuffer.resetMainTask();
        List<StubResult> afterResetMainTaskResult = makeDummyBuffer(3, 0);
        observingBuffer.buffering(afterResetMainTaskResult, dummyListener, new ObservingBuffer.Callback<StubResult, String[]>() {
            @Override
            public void onComplete(List<StubResult> results, String[] listener) {
                fail("should not call onComplete if main task reset");
            }
        });
    }

    private List<StubResult> makeDummyBuffer(int cnt, int offset) {
        List<StubResult> results = new ArrayList<>();
        for (int i = 0; i < cnt; ++i) {
            results.add(new StubResult(Integer.toString(i + offset)));
        }
        return results;
    }

}
