package cn.nukkit.scheduler;

import cn.nukkit.Server;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Nukkit Project Team
 */
public class AsyncPool extends ThreadPoolExecutor {

    private final Server server;

    public AsyncPool(Server server, int size) {
        super(size, size, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        this.setThreadFactory(runnable -> new Thread(runnable) {{
            setDaemon(true);
            setName(String.format("Nukkit Asynchronous Task Handler #%s", getPoolSize()));
        }});
        this.server = server;
    }

    @Override
    protected void afterExecute(Runnable runnable, Throwable throwable) {
        if (throwable != null) {
            server.getLogger().critical("Exception in asynchronous task", throwable);
        }
    }

    public Server getServer() {
        return server;
    }
}
