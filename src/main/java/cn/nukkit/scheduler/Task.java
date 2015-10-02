package cn.nukkit.scheduler;

/**
 * author: MagicDroidX
 * Nukkit
 */
public abstract class Task {
    private TaskHandler taskHandler = null;

    public final TaskHandler getHandler() {
        return this.taskHandler;
    }

    public final int getTaskId() {
        return this.taskHandler != null ? this.taskHandler.getTaskId() : -1;
    }

    public final void setHandler(TaskHandler taskHandler) {
        if (this.taskHandler == null || taskHandler == null) {
            this.taskHandler = taskHandler;
        }
    }

    public abstract void onRun(int currentTick);

    public void onCancel() {

    }

}
