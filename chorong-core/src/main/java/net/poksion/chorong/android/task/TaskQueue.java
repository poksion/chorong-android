package net.poksion.chorong.android.task;

public interface TaskQueue<T_Listener> {

    void enqueue(long taskId, Task<T_Listener> task);
    void execute(BlockingTask<T_Listener> task);
    <T_Result> void register(ObservingTask<T_Result, T_Listener> observingTask);

    int size();

}
