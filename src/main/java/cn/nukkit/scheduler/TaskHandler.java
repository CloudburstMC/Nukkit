package cn.nukkit.scheduler;

import cn.nukkit.plugin.Plugin;

/**
 * author: MagicDroidX
 * Nukkit
 */
public class TaskHandler {

    private final int taskId;
    private final boolean asynchronous;

    private final String timingName;
    private final Plugin plugin;
    private final Runnable task;

    private int delay;
    private int period;

    private int lastRunTick;
    private int nextRunTick;

    private boolean cancelled;

    public TaskHandler(Plugin plugin, String timingName, Runnable task, int taskId, boolean asynchronous) {
        this.asynchronous = asynchronous;
        this.plugin = plugin;
        this.task = task;
        this.taskId = taskId;
        this.timingName = timingName == null ? "Unknown" : timingName;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public int getNextRunTick() {
        return this.nextRunTick;
    }

    public void setNextRunTick(int nextRunTick) {
        this.nextRunTick = nextRunTick;
    }

    public int getTaskId() {
        return this.taskId;
    }

    public Runnable getTask() {
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

    public Plugin getPlugin() {
        return plugin;
    }

    public int getLastRunTick() {
        return lastRunTick;
    }

    public void setLastRunTick(int lastRunTick) {
        this.lastRunTick = lastRunTick;
    }

    public void cancel() {
        this.cancelled = true;
    }

    @Deprecated
    public void remove() {
        this.cancelled = true;
    }

    public void run(int currentTick) {
        setLastRunTick(currentTick);
        getTask().run();
    }

    public String getTaskName() {
        return this.timingName != null ? this.timingName : this.task.getClass().getName();
    }

    public boolean isAsynchronous() {
        return asynchronous;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

}
