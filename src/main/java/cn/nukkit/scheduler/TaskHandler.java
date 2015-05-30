package cn.nukkit.scheduler;

/**
 * author: MagicDroidX
 * Nukkit
 */
public class TaskHandler {

    protected Task task;
    protected long taskId;
    protected int delay;
    protected int period;
    protected int nextRun;
    protected boolean cancelled = false;
    //todo TimingsHandler
    protected String timingName;

    public TaskHandler(String timingName, Task task, long taskId) {
        this(timingName, task, taskId, -1, -1);
    }

    public TaskHandler(String timingName, Task task, long taskId, int delay) {
        this(timingName, task, taskId, delay, -1);
    }

    public TaskHandler(String timingName, Task task, long taskId, int delay, int period) {
        this.task = task;
        this.taskId = taskId;
        this.delay = delay;
        this.period = period;
        this.timingName = timingName == null ? "Unknown" : timingName;
        //todo getPluginTaskTimings
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public int getNextRun() {
        return this.nextRun;
    }

    public void setNextRun(int ticks) {
        this.nextRun = ticks;
    }

    public long getTaskId() {
        return this.taskId;
    }

    public Task getTask() {
        return this.task;
    }

    public int getDelay() {
        return this.delay;
    }

    public boolean isDelayed() {
        return this.delay > 0;
    }

    public boolean isRepeating() {
        return this.period > 0;
    }

    public int getPeriod() {
        return this.period;
    }

    /**
     * WARNING: Do not use this, it's only for internal use.
     * Changes to this function won't be recorded on the version.
     */
    public void cancel() {
        if (!this.isCancelled()) {
            this.task.onCancel();
        }
        this.remove();
    }

    public void remove() {
        this.cancelled = true;
        this.task.setHandler(null);
    }

    public void run(int currentTick) {
        this.task.onRun(currentTick);
    }

    public String getTaskName() {
        return this.timingName != null ? this.timingName : this.task.getClass().getName();
    }

}
