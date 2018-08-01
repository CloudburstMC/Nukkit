package com.nukkitx.server.scheduler;

import com.nukkitx.api.scheduler.NukkitRunnable;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;

/**
 * @author MagicDroidX
 */

@Log4j2
public class TaskHandler<T> implements com.nukkitx.api.scheduler.TaskHandler<T> {
    private final int taskId;
    private final boolean asynchronous;

    private final T plugin;
    private final Runnable task;

    private int delay;
    private int period;

    private int lastRunTick;
    private int nextRunTick;

    private boolean cancelled;

    public TaskHandler(T plugin, Runnable task, int taskId, boolean asynchronous) {
        this.asynchronous = asynchronous;
        this.plugin = plugin;
        this.task = task;
        this.taskId = taskId;
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

    public T getPlugin() {
        return plugin;
    }

    public int getLastRunTick() {
        return lastRunTick;
    }

    public void setLastRunTick(int lastRunTick) {
        this.lastRunTick = lastRunTick;
    }

    public void cancel() {
        if (!this.isCancelled() && this.task instanceof NukkitRunnable) {
            ((NukkitRunnable) this.task).onCancel();
        }
        this.cancelled = true;
    }

    @Deprecated
    public void remove() {
        this.cancelled = true;
    }

    public void run(int currentTick) {
        try {
            setLastRunTick(currentTick);
            getTask().run();
        } catch (RuntimeException ex) {
            log.log(Level.ERROR, "Exception while invoking run", ex);
        }
    }

    @Deprecated
    public String getTaskName() {
        return "Unknown";
    }

    public boolean isAsync() {
        return asynchronous;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

}
