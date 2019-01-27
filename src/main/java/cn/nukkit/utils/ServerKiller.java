package cn.nukkit.utils;

import java.util.concurrent.TimeUnit;

/**
 * author: MagicDroidX
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
        } catch (InterruptedException e) {
            // ignore
        }
        System.out.println("\nTook too long to stop, server was killed forcefully!\n");
        System.exit(1);
    }
}
