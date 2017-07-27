package net.poksion.chorong.android.task;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ObservingBuffer<T_Result extends ObservingBuffer.Unique, T_Listener> {

    public interface Unique {
        String getId();
    }

    public interface Callback<T_Result, T_Listener> {
        void onComplete(List<T_Result> results, T_Listener listener);
    }

    private boolean taskDone = false;
    private final Queue<T_Result> queue = new ConcurrentLinkedQueue<>();

    public void resetMainTask() {
        taskDone = false;
        queue.clear();
    }

    public void completeMainTask(List<T_Result> results, T_Listener listener, Callback<T_Result, T_Listener> callback) {
        taskDone = true;

        List<T_Result> bufferedResults = new ArrayList<>();

        Set<String> ids = new HashSet<>();
        for (T_Result result : results) {
            bufferedResults.add(result);
            ids.add(result.getId());
        }

        for (T_Result result : queue) {
            if (!ids.contains(result.getId())) {
                bufferedResults.add(result);
            }
        }

        queue.clear();

        callback.onComplete(bufferedResults, listener);
    }

    public void buffering(List<T_Result> results, T_Listener listener, Callback<T_Result, T_Listener> callback) {
        if (taskDone) {
            completeMainTask(results, listener, callback);
        } else {
            queue.addAll(results);
        }
    }

}
