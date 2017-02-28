package net.poksion.chorong.android.task;

public class TaskQueueRunner<T_Listener> implements TaskRunner<T_Listener> {

    private final TaskQueue<T_Listener> taskQueue;

    public TaskQueueRunner(TaskQueue<T_Listener> taskQueue) {
        this.taskQueue = taskQueue;
    }

    @Override
    public void runTask(Task<T_Listener> task) {
        long taskId = System.currentTimeMillis();
        taskQueue.enqueue(taskId, task);
    }

    @Override
    public void runBlockingTask(BlockingTask<T_Listener> blockingTask) {
        taskQueue.execute(blockingTask);
    }
}
