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
        void onComplete(List<ResultT> results, ListenerT listener);
    }

    private boolean taskDone = false;
    private final Queue<ResultT> queue = new ConcurrentLinkedQueue<>();

    public void resetMainTask() {
        taskDone = false;
        queue.clear();
    }

    public void completeMainTask(List<ResultT> results, ListenerT listener, Callback<ResultT, ListenerT> callback) {
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

        callback.onComplete(bufferedResults, listener);
    }

    public void buffering(List<ResultT> results, ListenerT listener, Callback<ResultT, ListenerT> callback) {
        if (taskDone) {
            completeMainTask(results, listener, callback);
        } else {
            queue.addAll(results);
        }
    }

}
