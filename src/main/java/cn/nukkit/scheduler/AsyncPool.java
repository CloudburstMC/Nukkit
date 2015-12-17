package cn.nukkit.scheduler;

import cn.nukkit.Server;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class AsyncPool {

    private final ThreadPoolExecutor pool;
    private final Server server;
    private final int size;
    private final AtomicInteger currentThread;

    public AsyncPool(Server server, int size) {
        this.currentThread = new AtomicInteger();
        this.size = size;
        this.pool = new ThreadPoolExecutor(size, size, 0,
                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(),
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

    public int getSize() {
        return size;
    }

}
