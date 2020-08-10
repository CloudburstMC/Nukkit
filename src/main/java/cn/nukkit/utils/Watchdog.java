package cn.nukkit.utils;

import cn.nukkit.Server;

import java.lang.management.ManagementFactory;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;

public class Watchdog extends Thread {

    private final Server server;
    private final long time;
    public volatile boolean running;
    private boolean responding = true;

    public Watchdog(Server server, long time) {
        this.server = server;
        this.time = time;
        this.running = true;
        this.setName("Watchdog");
        this.setDaemon(true);
    }

    public void kill() {
        running = false;
        interrupt();
    }

    @Override
    public void run() {
        while (this.running) {
            long current = server.getNextTick();
            if (current != 0) {
                long diff = System.currentTimeMillis() - current;
                if (!responding && diff > time * 2) {
                    System.exit(1); // Kill the server if it gets stuck on shutdown
                }
                
                if (diff <= time) {
                    responding = true;
                } else if (responding) {
                    MainLogger logger = this.server.getLogger();
                    logger.emergency("--------- Server stopped responding --------- (" + Math.round(diff / 1000d) + "s)");
                    logger.emergency("Please report this to PowerNukkit:");
                    logger.emergency(" - https://github.com/PowerNukkit/PowerNukkit/issues/new");
                    logger.emergency("---------------- Main thread ----------------");

                    dumpThread(ManagementFactory.getThreadMXBean().getThreadInfo(this.server.getPrimaryThread().getId(), Integer.MAX_VALUE), logger);

                    logger.emergency("---------------- All threads ----------------");
                    ThreadInfo[] threads = ManagementFactory.getThreadMXBean().dumpAllThreads(true, true);
                    for (int i = 0; i < threads.length; i++) {
                        if (i != 0) logger.emergency("------------------------------");
                        dumpThread(threads[i], logger);
                    }
                    logger.emergency("---------------------------------------------");
                    responding = false;
                    this.server.forceShutdown();
                }
            }
            try {
                sleep(Math.max(time / 4, 1000));
            } catch (InterruptedException interruption) {
                server.getLogger().emergency("The Watchdog Thread has been interrupted and is no longer monitoring the server state");
                running = false;
                return;
            }
        }
        server.getLogger().warning("Watchdog was stopped");
    }

    private static void dumpThread(ThreadInfo thread, Logger logger) {
        logger.emergency("Current Thread: " + thread.getThreadName());
        logger.emergency("\tPID: " + thread.getThreadId() + " | Suspended: " + thread.isSuspended() + " | Native: " + thread.isInNative() + " | State: " + thread.getThreadState());
        // Monitors
        if (thread.getLockedMonitors().length != 0) {
            logger.emergency("\tThread is waiting on monitor(s):");
            for (MonitorInfo monitor : thread.getLockedMonitors()) {
                logger.emergency("\t\tLocked on:" + monitor.getLockedStackFrame());
            }
        }

        logger.emergency("\tStack:");
        for (StackTraceElement stack : thread.getStackTrace()) {
            logger.emergency("\t\t" + stack);
        }
    }
}
