package net.poksion.chorong.android.task;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ObservingBuffer<ResultT extends ObservingBuffer.Unique, ListenerT> {

    public interface Unique {
        String getId();
    }

    public interface Callback<ResultT, ListenerT> {
        void completeOnMain(List<ResultT> results, ListenerT listener);
        void completeOnSub(List<ResultT> results, ListenerT listener);
    }

    private boolean taskDone = false;
    private final Callback<ResultT, ListenerT> callback;
    private final Queue<ResultT> queue = new ConcurrentLinkedQueue<>();

    public ObservingBuffer(Callback<ResultT, ListenerT> callback) {
        this.callback = callback;
    }

    public void startMain() {
        taskDone = false;
        queue.clear();
    }

    public void completeMain(List<ResultT> results, ListenerT listener) {
        complete(true, results, listener);
    }

    public void listenSub(List<ResultT> results, ListenerT listener) {
        if (taskDone) {
            complete(false, results, listener);
        } else {
            queue.addAll(results);
        }
    }

    private void complete(boolean main, List<ResultT> results, ListenerT listener) {
        taskDone = true;

        List<ResultT> bufferedResults = new ArrayList<>();
        Set<String> ids = new HashSet<>();

        for (ResultT result : results) {
            bufferedResults.add(result);
            ids.add(result.getId());
        }

        for (ResultT result : queue) {
            if (!ids.contains(result.getId())) {
                bufferedResults.add(result);
            }
        }
        queue.clear();

        if (main) {
            callback.completeOnMain(bufferedResults, listener);
        } else {
            callback.completeOnSub(bufferedResults, listener);
        }
    }

}
