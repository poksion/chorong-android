package net.poksion.chorong.android.task;

public class TaskQueueRunner<ListenerT> implements TaskRunner<ListenerT> {

    private final TaskQueue<ListenerT> taskQueue;

    public TaskQueueRunner(TaskQueue<ListenerT> taskQueue) {
        this.taskQueue = taskQueue;
    }

    @Override
    public void runTask(Task<ListenerT> task) {
        long taskId = System.currentTimeMillis();
        taskQueue.enqueue(taskId, task);
    }

    @Override
    public void runBlockingTask(BlockingTask<ListenerT> blockingTask) {
        taskQueue.execute(blockingTask);
    }

    @Override
    public <ResultT> void registerObservingTask(ObservingTask<ResultT, ListenerT> observingTask) {
        taskQueue.register(observingTask);
    }

}
