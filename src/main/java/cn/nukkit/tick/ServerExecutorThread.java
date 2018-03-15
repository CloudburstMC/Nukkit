package cn.nukkit.tick;

import cn.nukkit.Server;

/**
 * @author DaPorkchop_
 *
 * A worker thread that does anything during a server tick.
 */
public abstract class ServerExecutorThread extends Thread {
    public final Server server;
    public final ServerTickManager tickManager;

    public ServerExecutorThread(Server server, ServerTickManager tickManager)  {
        this.server = server;
        this.tickManager = tickManager;
    }

    @Override
    public void run() {
        while (server.isRunning)  {
            doRun();
            tickManager.onWorkerFinish();
        }
    }

    /**
     * Do any sort of logic that needs to run every server tick
     *
     * Don't use these worker threads for any old tick handler though or things will become sluggish
     */
    public abstract void doRun();
}
