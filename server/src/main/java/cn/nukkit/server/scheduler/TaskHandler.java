/*
 * GNU GENERAL PUBLIC LICENSE
 * Copyright (C) 2018 NukkitX Project
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * verion 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * Contact info: info@nukkitx.com
 */

package cn.nukkit.server.scheduler;

import cn.nukkit.api.plugin.Plugin;
import cn.nukkit.api.scheduler.NukkitRunnable;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;

/**
 * @author MagicDroidX
 */

@Log4j2
public class TaskHandler implements cn.nukkit.api.scheduler.TaskHandler {
    private final int taskId;
    private final boolean asynchronous;

    private final Plugin plugin;
    private final Runnable task;

    private int delay;
    private int period;

    private int lastRunTick;
    private int nextRunTick;

    private boolean cancelled;

    public TaskHandler(Plugin plugin, Runnable task, int taskId, boolean asynchronous) {
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
