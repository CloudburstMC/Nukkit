package cn.nukkit.scheduler;

import cn.nukkit.Server;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Nukkit Project Team
 */
public class AsyncPool {

    private final ThreadPoolExecutor pool;
    private final Server server;
    private final int size;
    private final AtomicInteger currentThread;

    public AsyncPool(Server server, int size) {
        this.currentThread = new AtomicInteger();
        this.size = size;
        this.pool = new ThreadPoolExecutor(size, Integer.MAX_VALUE,
                60, TimeUnit.MILLISECONDS, new SynchronousQueue<>(),
                runnable -> new Thread(runnable) {{
                    setDaemon(true);
                    setName(String.format("Nukkit Asynchronous Task Handler #%s", currentThread.incrementAndGet()));
                }}
        );
        this.server = server;
    }

    public void submitTask(Runnable runnable) {
        pool.execute(runnable);
    }

    public Server getServer() {
        return server;
    }

    public int getSize() {
        return size;
    }

}
