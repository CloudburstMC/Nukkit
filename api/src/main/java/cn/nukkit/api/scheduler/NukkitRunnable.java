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

package cn.nukkit.api.scheduler;

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