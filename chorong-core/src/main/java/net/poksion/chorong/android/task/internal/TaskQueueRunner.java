package net.poksion.chorong.android.task.internal;

import net.poksion.chorong.android.task.BlockingTask;
import net.poksion.chorong.android.task.Task;
import net.poksion.chorong.android.task.TaskRunner;

public class TaskQueueRunner<T_Listener> implements TaskRunner<T_Listener> {

    protected final TaskQueue<T_Listener> taskQueue;

    protected TaskQueueRunner(TaskQueue<T_Listener> taskQueue) {
        this.taskQueue = taskQueue;
    }

    public void runTask(Task<T_Listener> task) {
        taskQueue.run(task);
    }

    public void runBlockingTask(BlockingTask<T_Listener> blockingTask) {
        taskQueue.run(blockingTask);
    }
}
