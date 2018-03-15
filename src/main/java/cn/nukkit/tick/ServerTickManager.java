package cn.nukkit.tick;

import cn.nukkit.Server;
import cn.nukkit.tick.thread.NetworkingTickThread;

/**
 * @author DaPorkchop_
 *
 * Manages all the worker threads, informing them of when to start
 */
public class ServerTickManager {
    private final Server server;

    public ServerTickManager(Server server) {
        this.server = server;

        this.addWorkerThread(new NetworkingTickThread(server, this));
    }

    private final Object syncPostTickWaitQueue = new Object();
    private final Object syncEndOfTick = new Object();

    private int totalThreadCount = 0;
    private int waitingOnThreads = 0;

    public void tick()  {
        if (this.waitingOnThreads != 0)  {
            throw new IllegalStateException("Attempted to tick server before ending previous tick!");
        }

        TickRate.INSTANCE.update();
        this.server.tickCounter++;

        //wait on all threads
        this.waitingOnThreads = this.totalThreadCount;

        //notify all threads (since the previous tick is finished, they're all waiting on the dummy object)
        //this is where the actual tick starts doing things
        this.syncPostTickWaitQueue.notifyAll();

        try {
            //wait for all worker threads to finish execution before proceeding to the next tick
            this.syncEndOfTick.wait();
        } catch (InterruptedException e)    {
            this.server.getLogger().logException(e);
        }

        this.server.doPostTick();
    }

    public void shutdown()  {
        this.server.isRunning = false;

        //notify workers so that they stop waiting and then terminate as the server is flagged as not running
        this.syncEndOfTick.notifyAll();
    }

    public void onWorkerFinish()   {
        //decrement the waiting thread count to check if the tick is finished
        if (--this.waitingOnThreads <= 0)    {
            this.syncEndOfTick.notifyAll();
        }

        try {
            //wait on the dummy object
            //this object won't be notified until the next tick starts
            this.syncPostTickWaitQueue.wait();
        } catch (InterruptedException e)    {
            this.server.getLogger().logException(e);
        }
    }

    public void addWorkerThread(ServerExecutorThread thread)    {
        if (thread.server == this.server && thread.tickManager == this) {
            totalThreadCount++;
        }
    }
}
