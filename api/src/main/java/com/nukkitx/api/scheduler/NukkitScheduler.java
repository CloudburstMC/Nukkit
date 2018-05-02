package com.nukkitx.api.scheduler;

import com.nukkitx.api.plugin.Plugin;

/**
 * @author CreeperFace
 */
public interface NukkitScheduler {

    TaskHandler scheduleTask(Plugin plugin, Runnable task);

    TaskHandler scheduleRepeatingTask(Plugin plugin, Runnable task, int period);

    TaskHandler scheduleDelayedTask(Plugin plugin, Runnable task, int delay);

    TaskHandler scheduleDelayedRepeatingTask(Plugin plugin, Runnable task, int delay, int period);

    TaskHandler scheduleAsyncTask(Plugin plugin, Runnable task);

    void cancelTask(int taskId);

    void cancelAllTasks();

    void cancelTasks(Plugin plugin);

    boolean isQueued(int taskId);
}
