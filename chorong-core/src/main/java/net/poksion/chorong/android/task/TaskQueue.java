package net.poksion.chorong.android.task;

public interface TaskQueue<ListenerT> {

    void enqueue(long taskId, Task<ListenerT> task);
    void execute(BlockingTask<ListenerT> task);
    <ResultT> void register(ObservingTask<ResultT, ListenerT> observingTask);

    int size();

}
