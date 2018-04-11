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

import cn.nukkit.server.NukkitServer;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.ForkJoinTask;

@Log4j2
public abstract class AsyncTask<V> extends ForkJoinTask<V> {

    private V result;
    private int taskId;
    private boolean finished = false;

    @Override
    public boolean exec() {
        this.result = null;
        this.run();
        this.finished = true;
        return true;
    }

    public boolean isFinished() {
        return this.finished;
    }

    public V getResult() {
        return this.result;
    }

    public void setResult(V result) {
        this.result = result;
    }

    public boolean hasResult() {
        return this.result != null;
    }

    public int getTaskId() {
        return this.taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public abstract void run();

    public void onCompletion(NukkitServer server) {

    }

    public void cleanObject() {
        this.result = null;
        this.taskId = 0;
        this.finished = false;
    }
}
