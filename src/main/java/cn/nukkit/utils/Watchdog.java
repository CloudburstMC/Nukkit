package cn.nukkit.utils;

import cn.nukkit.Server;
import lombok.extern.log4j.Log4j2;

import java.lang.management.ManagementFactory;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;

@Log4j2
public class Watchdog extends Thread {

    private final Server server;
    private final long time;
    public boolean running;
    private boolean responding = true;

    public Watchdog(Server server, long time) {
        this.server = server;
        this.time = time;
        this.running = true;
        this.setName("Watchdog");
    }

    public void kill() {
        running = false;
        synchronized (this) {
            this.notifyAll();
        }
    }

    private static void dumpThread(ThreadInfo thread) {
        log.fatal("Current Thread: " + thread.getThreadName());
        log.fatal("\tPID: " + thread.getThreadId() + " | Suspended: " + thread.isSuspended() + " | Native: " + thread.isInNative() + " | State: " + thread.getThreadState());
        // Monitors
        if (thread.getLockedMonitors().length != 0) {
            log.fatal("\tThread is waiting on monitor(s):");
            for (MonitorInfo monitor : thread.getLockedMonitors()) {
                log.fatal("\t\tLocked on:" + monitor.getLockedStackFrame());
            }
        }

        log.fatal("\tStack:");
        for (StackTraceElement stack : thread.getStackTrace()) {
            log.fatal("\t\t" + stack);
        }
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
                if (server.isRunning() && diff > time) {
                    if (responding) {

                        log.fatal("--------- Server stopped responding --------- (" + Math.round(diff / 1000d) + "s)");
                        log.fatal("Please report this to Nukkit:");
                        log.fatal(" - https://github.com/NukkitX/Nukkit/issues/new");
                        log.fatal("---------------- Main thread ----------------");

                        dumpThread(ManagementFactory.getThreadMXBean().getThreadInfo(this.server.getPrimaryThread().getId(), Integer.MAX_VALUE));

                        log.fatal("---------------- All threads ----------------");
                        ThreadInfo[] threads = ManagementFactory.getThreadMXBean().dumpAllThreads(true, true);
                        for (int i = 0; i < threads.length; i++) {
                            if (i != 0) log.fatal("------------------------------");
                            dumpThread(threads[i]);
                        }
                        log.fatal("---------------------------------------------");
                        responding = false;
                        this.server.forceShutdown();
                    }
                } else {
                    responding = true;
                }
            }
            try {
                synchronized (this) {
                    this.wait(Math.max(time / 4, 1000));
                }
            } catch (InterruptedException ignore) {}
        }
    }
}
