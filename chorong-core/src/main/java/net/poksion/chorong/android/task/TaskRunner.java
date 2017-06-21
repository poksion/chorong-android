package net.poksion.chorong.android.task;

public interface TaskRunner<T_Listener> {

    void runTask(Task<T_Listener> task);

    void runBlockingTask(BlockingTask<T_Listener> blockingTask);
    <T_Result> void registerObservingTask(ObservingTask<T_Result, T_Listener> observingTask);

}
