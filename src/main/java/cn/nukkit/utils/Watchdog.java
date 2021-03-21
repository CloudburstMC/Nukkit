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
            StringBuilder sb = new StringBuilder("--------- The finalizer thread didn't complete in time! ---------").append('\n')
                .append("This detection means that the finalizer thread may be stuck and").append('\n')
                .append("RAM memory might be leaking!").append('\n')
                .append(" - https://github.com/PowerNukkit/PowerNukkit/issues/new").append('\n')
                .append("---------------- ForcedFinalizer ----------------").append('\n');
            dumpThread(ManagementFactory.getThreadMXBean().getThreadInfo(forcedFinalizer.getId(), Integer.MAX_VALUE), sb);
            sb.append("-------------------------------------------------");
            log.fatal(sb.toString());
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
                    StringBuilder builder = new StringBuilder(
                            "--------- Server stopped responding --------- (" + Math.round(diff / 1000d) + "s)").append('\n')
                        .append("Please report this to PowerNukkit:").append('\n')
                        .append(" - https://github.com/PowerNukkit/PowerNukkit/issues/new").append('\n')
                        .append("---------------- Main thread ----------------").append('\n');

                    dumpThread(ManagementFactory.getThreadMXBean().getThreadInfo(this.server.getPrimaryThread().getId(), Integer.MAX_VALUE), builder);

                    builder.append("---------------- All threads ----------------").append('\n');
                    ThreadInfo[] threads = ManagementFactory.getThreadMXBean().dumpAllThreads(true, true);
                    for (int i = 0; i < threads.length; i++) {
                        if (i != 0) builder.append("------------------------------").append('\n');
                        dumpThread(threads[i], builder);
                    }
                    builder.append("---------------------------------------------").append('\n');
                    log.fatal(builder.toString());
                    responding = false;
                    this.server.forceShutdown();
                }
            }
            try {
                sleep(Math.max(time / 4, 1000));
            } catch (InterruptedException interruption) {
                log.fatal("The Watchdog Thread has been interrupted and is no longer monitoring the server state", interruption);
                running = false;
                return;
            }
        }
        log.warn("Watchdog was stopped");
    }

    private static void dumpThread(ThreadInfo thread, StringBuilder builder) {
        if (thread == null) {
            builder.append("Attempted to dump a null thread!").append('\n');
            return;
        }
        builder.append("Current Thread: " + thread.getThreadName()).append('\n');
        builder.append("\tPID: " + thread.getThreadId() + " | Suspended: " + thread.isSuspended() + " | Native: " + thread.isInNative() + " | State: " + thread.getThreadState()).append('\n');
        // Monitors
        if (thread.getLockedMonitors().length != 0) {
            builder.append("\tThread is waiting on monitor(s):").append('\n');
            for (MonitorInfo monitor : thread.getLockedMonitors()) {
                builder.append("\t\tLocked on:" + monitor.getLockedStackFrame()).append('\n');
            }
        }

        builder.append("\tStack:").append('\n');
        for (StackTraceElement stack : thread.getStackTrace()) {
            builder.append("\t\t" + stack).append('\n');
        }
    }
}
