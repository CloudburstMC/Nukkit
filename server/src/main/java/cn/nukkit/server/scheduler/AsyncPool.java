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

import java.util.concurrent.ForkJoinPool;

@Log4j2
public class AsyncPool extends ForkJoinPool {

    private final NukkitServer server;

    public AsyncPool(NukkitServer server, int size) {
        super(size, defaultForkJoinWorkerThreadFactory, new AsyncExceptionHandler(), true);
        this.server = server;
    }

    public NukkitServer getServer() {
        return server;
    }

    private static class AsyncExceptionHandler implements Thread.UncaughtExceptionHandler {

        @Override
        public void uncaughtException(Thread thread, Throwable throwable) {
            log.fatal("Exception in asynchronous task on thread {}\n{}", thread.getName(), throwable);
        }
    }
}
