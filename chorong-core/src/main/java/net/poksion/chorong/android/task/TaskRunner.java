package net.poksion.chorong.android.task;

public interface TaskRunner<T_Listener> {

    void runBlockingTask(BlockingTask<T_Listener> blockingTask);
    void runTask(Task<T_Listener> task);

}
