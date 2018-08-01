package com.nukkitx.api.scheduler;

/**
 * @author CreeperFace
 */
public interface NukkitScheduler {

    <T> TaskHandler<T> scheduleTask(T plugin, Runnable task);

    <T> TaskHandler<T> scheduleRepeatingTask(T plugin, Runnable task, int period);

    <T> TaskHandler<T> scheduleDelayedTask(T plugin, Runnable task, int delay);

    <T> TaskHandler<T> scheduleDelayedRepeatingTask(T plugin, Runnable task, int delay, int period);

    <T> TaskHandler<T> scheduleAsyncTask(T plugin, Runnable task);

    void cancelTask(int taskId);

    void cancelAllTasks();

    <T> void cancelTasks(T plugin);

    boolean isQueued(int taskId);
}
