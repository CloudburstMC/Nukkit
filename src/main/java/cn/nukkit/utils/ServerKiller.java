package cn.nukkit.utils;

import java.util.concurrent.TimeUnit;

/**
 * A task that kills the server process after given time.
 *
 * @author MagicDroidX
 * Nukkit Project
 */
public class ServerKiller extends Thread {

    public final long sleepTime;

    public ServerKiller(long time) {
        this(time, TimeUnit.SECONDS);
    }

    public ServerKiller(long time, TimeUnit unit) {
        this.sleepTime = unit.toMillis(time);
        this.setName("Server Killer");
    }

    @Override
    public void run() {
        try {
            sleep(sleepTime);
        } catch (InterruptedException ignored) {}
        System.out.println("Took too long to stop, server was killed forcefully!");
        System.exit(1);
    }
}
