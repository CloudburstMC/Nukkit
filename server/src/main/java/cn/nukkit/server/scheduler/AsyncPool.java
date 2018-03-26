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
