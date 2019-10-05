package cn.nukkit.scheduler;

import lombok.extern.log4j.Log4j2;

import java.util.concurrent.ForkJoinPool;

/**
 * @author Nukkit Project Team
 */
@Log4j2
public class AsyncPool extends ForkJoinPool {
    private static final int MAX_CAP = 0x7fff;

    private static final Thread.UncaughtExceptionHandler UNCAUGHT_EXCEPTION_HANDLER = (t, e) -> {
        log.fatal("Exception in scheduled task on thread: " + t.getName(), e);
    };

    private AsyncPool(int parallelism) {
        super(parallelism, ForkJoinPool.defaultForkJoinWorkerThreadFactory, UNCAUGHT_EXCEPTION_HANDLER, true);
    }

    static AsyncPool makePool(int parallelism) {
        if (parallelism < 0 && // default 1 less than #cores
                (parallelism = Runtime.getRuntime().availableProcessors() - 1) <= 0)
            parallelism = 1;
        if (parallelism > MAX_CAP)
            parallelism = MAX_CAP;
        return new AsyncPool(parallelism);
    }

//    public AsyncPool(Server server, int size) {
//        super(size, Integer.MAX_VALUE, 60, TimeUnit.MILLISECONDS, new SynchronousQueue<>());
//        this.setThreadFactory(runnable -> new Thread(runnable) {{
//            setDaemon(true);
//            setName(String.format("Nukkit Asynchronous Task Handler #%s", getPoolSize()));
//        }});
//        this.server = server;
//    }
//
//    @Override
//    protected void afterExecute(Runnable runnable, Throwable throwable) {
//        if (throwable != null) {
//            log.
//            server.getLogger().critical("Exception in asynchronous task", throwable);
//        }
//    }
}
