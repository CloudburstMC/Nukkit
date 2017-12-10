package cn.nukkit.server.scheduler;

import cn.nukkit.server.NukkitServer;

/**
 * This class is provided as an easy way to handle scheduling tasks.
 */
public abstract class NukkitRunnable implements Runnable {
    private TaskHandler taskHandler;

    /**
     * Attempts to cancel this task.
     *
     * @throws IllegalStateException if task was not scheduled yet
     */
    public synchronized void cancel() throws IllegalStateException {
        taskHandler.cancel();
    }

    public synchronized Runnable runTask(Plugin plugin) throws IllegalArgumentException, IllegalStateException {
        checkState();
        this.taskHandler = NukkitServer.getInstance().getScheduler().scheduleTask(plugin, this);
        return taskHandler.getTask();
    }

    public synchronized Runnable runTaskAsynchronously(Plugin plugin) throws IllegalArgumentException, IllegalStateException {
        checkState();
        this.taskHandler = NukkitServer.getInstance().getScheduler().scheduleTask(plugin, this, true);
        return taskHandler.getTask();
    }

    public synchronized Runnable runTaskLater(Plugin plugin, int delay) throws IllegalArgumentException, IllegalStateException {
        checkState();
        this.taskHandler = NukkitServer.getInstance().getScheduler().scheduleDelayedTask(plugin, this, delay);
        return taskHandler.getTask();
    }

    public synchronized Runnable runTaskLaterAsynchronously(Plugin plugin, int delay) throws IllegalArgumentException, IllegalStateException {
        checkState();
        this.taskHandler = NukkitServer.getInstance().getScheduler().scheduleDelayedTask(plugin, this, delay, true);
        return taskHandler.getTask();
    }

    public synchronized Runnable runTaskTimer(Plugin plugin, int delay, int period) throws IllegalArgumentException, IllegalStateException {
        checkState();
        this.taskHandler = NukkitServer.getInstance().getScheduler().scheduleDelayedRepeatingTask(plugin, this, delay, period);
        return taskHandler.getTask();
    }

    public synchronized Runnable runTaskTimerAsynchronously(Plugin plugin, int delay, int period) throws IllegalArgumentException, IllegalStateException {
        checkState();
        this.taskHandler = NukkitServer.getInstance().getScheduler().scheduleDelayedRepeatingTask(plugin, this, delay, period, true);
        return taskHandler.getTask();
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

    private void checkState() {
        if (taskHandler != null) {
            throw new IllegalStateException("Already scheduled as " + taskHandler.getTaskId());
        }
    }
}