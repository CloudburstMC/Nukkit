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

    public ServerExecutorThread(Server server, ServerTickManager tickManager, String name)  {
        super(name);
        this.server = server;
        this.tickManager = tickManager;
        this.start();
    }

    @Override
    public void run() {
        tickManager.workerWait();

        while (server.isRunning)  {
            doRun();
            //shutdown is called by tick threads, this prevents an infinite shutdown cycle where one thread never exits
            if (server.isRunning) {
                tickManager.onWorkerFinish();
            }
        }

        tickManager.onWorkerShutdown();
    }

    /**
     * Do any sort of logic that needs to run every server tick
     *
     * Don't use these worker threads for any old tick handler though or things will become sluggish
     */
    public abstract void doRun();
}
