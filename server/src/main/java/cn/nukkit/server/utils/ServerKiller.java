package cn.nukkit.server.utils;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ServerKiller extends Thread {

    public final int time;

    public ServerKiller() {
        this(15);
    }

    public ServerKiller(int time) {
        this.time = time;
        this.setName("Server Killer");
    }

    @Override
    public void run() {
        try {
            sleep(this.time * 1000);
        } catch (InterruptedException e) {
            // ignore
        }
        System.out.println("\nTook too long to stop, NukkitServer was killed forcefully!\n");
        System.exit(1);
    }

}
