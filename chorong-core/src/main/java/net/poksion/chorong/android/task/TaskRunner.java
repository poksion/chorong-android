package net.poksion.chorong.android.task;

public interface TaskRunner<ListenerT> {

    void runTask(Task<ListenerT> task);

    void runBlockingTask(BlockingTask<ListenerT> blockingTask);
        <ResultT> void registerObservingTask(ObservingTask<ResultT, ListenerT> observingTask);

}
