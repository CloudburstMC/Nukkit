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
    public volatile boolean running;
    private boolean responding = true;
    private Thread forcedFinalizer;
    private boolean warnedAboutFinalizer;

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
    
    private void checkFinalizer() {
        if (forcedFinalizer != null && forcedFinalizer.isAlive()) {
            log.fatal("--------- The finalizer thread didn't complete in time! ---------");
            log.fatal("This detection means that the finalizer thread may be stuck and");
            log.fatal("RAM memory might be leaking!");
            log.fatal(" - https://github.com/PowerNukkit/PowerNukkit/issues/new");
            log.fatal("---------------- ForcedFinalizer ----------------");
            dumpThread(ManagementFactory.getThreadMXBean().getThreadInfo(forcedFinalizer.getId(), Integer.MAX_VALUE));
            log.fatal("-------------------------------------------------");
            warnedAboutFinalizer = true;
        } else {
            if (warnedAboutFinalizer) {
                log.warn("The ForcedFinalizer has finished");
                warnedAboutFinalizer = false;
            }
            forcedFinalizer = new Thread(()-> {
                log.trace("Forcing finalization");
                System.runFinalization();
                log.trace("Forced finalization completed");
            });
            forcedFinalizer.setName("ForcedFinalizer");
            forcedFinalizer.setDaemon(true);
            forcedFinalizer.start();
        }
    }

    @Override
    public void run() {
        while (this.running) {
            checkFinalizer();
            long current = server.getNextTick();
            if (current != 0) {
                long diff = System.currentTimeMillis() - current;
                if (!responding && diff > time * 2) {
                    System.exit(1); // Kill the server if it gets stuck on shutdown
                }
                
                if (diff <= time) {
                    responding = true;
                } else if (responding) {
                    log.fatal("--------- Server stopped responding --------- (" + Math.round(diff / 1000d) + "s)");
                    log.fatal("Please report this to PowerNukkit:");
                    log.fatal(" - https://github.com/PowerNukkit/PowerNukkit/issues/new");
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

    private static void dumpThread(ThreadInfo thread) {
        if (thread == null) {
            log.fatal("Attempted to dump a null thread!");
            return;
        }
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
}
