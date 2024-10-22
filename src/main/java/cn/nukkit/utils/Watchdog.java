package cn.nukkit.utils;

import cn.nukkit.Server;

import java.lang.management.ManagementFactory;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;

/**
 * Watchdog monitors the server's main thread and kills the server if it gets frozen.
 */
public class Watchdog extends Thread {

    private final Server server;
    /**
     * Watchdog threshold
     */
    public volatile long time;
    /**
     * Watchdog running
     */
    public volatile boolean running;

    public Watchdog(Server server, long time) {
        this.server = server;
        this.time = time;
        this.running = true;
        this.setName("Watchdog");
        this.setDaemon(true);
    }

    /**
     * Disable Watchdog
     */
    public void kill() {
        this.running = false;
        this.interrupt();
    }

    @Override
    public void run() {
        while (this.running) {
            long current = this.server.getNextTick();
            if (current != 0) {
                long diff = System.currentTimeMillis() - current;
                if (diff > this.time) {
                    if (this.server.isRunning()) {
                        MainLogger logger = this.server.getLogger();
                        long lastResponse = Math.round(diff / 1000d);

                        logger.emergency("--------- Server stopped responding ---------");
                        logger.emergency("Last response " + lastResponse + " seconds ago");
                        logger.emergency("---------------- Main thread ----------------");

                        ThreadInfo mainThread = ManagementFactory.getThreadMXBean().getThreadInfo(this.server.getPrimaryThread().getId(), Integer.MAX_VALUE);
                        dumpThread(mainThread, logger);

                        logger.emergency("---------------- All threads ----------------");
                        ThreadInfo[] threads = ManagementFactory.getThreadMXBean().dumpAllThreads(true, true);
                        for (int i = 0; i < threads.length; i++) {
                            if (i != 0) logger.emergency("------------------------------");
                            dumpThread(threads[i], logger);
                        }
                        logger.emergency("---------------------------------------------");

                        if ("TIMED_WAITING".equals(mainThread.getThreadState().toString())) {
                            logger.warning("Make sure your plugins are not calling sleep() on main thread and that your terminal doesn't suspend server process when not focused");
                        }

                        this.server.forceShutdown("§cServer stopped responding");
                    } else if (diff > time << 1) {
                        System.out.println("\nTook too long to stop, server was killed forcefully!\n");
                        System.exit(1);
                        return;
                    }
                }
            }
            try {
                Thread.sleep(Math.max(this.time >> 2, 1000));
            } catch (InterruptedException ignore) {
                if (this.running) {
                    this.running = false;
                    this.server.getLogger().emergency("The Watchdog thread has been interrupted and is no longer monitoring the server state");
                }
                return;
            }
        }
    }

    /**
     * Dump thread stack trace
     *
     * @param thread thread to dump
     * @param logger logger
     */
    private static void dumpThread(ThreadInfo thread, Logger logger) {
        logger.emergency("Thread: " + thread.getThreadName());
        logger.emergency("\tPID: " + thread.getThreadId() + " | Suspended: " + thread.isSuspended() + " | Native: " + thread.isInNative() + " | State: " + thread.getThreadState());

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
