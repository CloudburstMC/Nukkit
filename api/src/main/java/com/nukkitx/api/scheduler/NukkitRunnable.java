package com.nukkitx.api.scheduler;

/**
 * This class is provided as an easy way to handle scheduling tasks.
 */
public abstract class NukkitRunnable implements Runnable {

    private int taskId;
    private TaskHandler taskHandler;

    /**
     * Attempts to cancel this task.
     */
    public synchronized void cancel() {
        taskHandler.cancel();
    }

    public void onCancel() {

    }

    /**
     * Gets the task id for this runnable.
     *
     * @return the task id that this runnable was scheduled as
     * @throws IllegalStateException if task was not scheduled yet
     */
    public synchronized int getTaskId() throws IllegalStateException {
        if (taskHandler == null) {
            throw new IllegalStateException("Not scheduled yet");
        }
        final int id = taskHandler.getTaskId();
        return id;
    }

    public void setHandler(TaskHandler taskHandler) {
        if (this.taskHandler != null) {
            throw new IllegalStateException("TaskHandler has been already set");
        }
        this.taskHandler = taskHandler;
    }
}