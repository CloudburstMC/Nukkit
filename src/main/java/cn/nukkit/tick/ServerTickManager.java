package cn.nukkit.tick;

import cn.nukkit.Server;
import cn.nukkit.tick.thread.*;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author DaPorkchop_
 * <p>
 * Manages all the worker threads, informing them of when to start
 */
public class ServerTickManager {
    private final Server server;

    public ServerTickManager(Server server) {
        this.server = server;

        this.addWorkerThread(new NetworkingTickThread(server, this));
        this.addWorkerThread(new AutoSaveTickThread(server, this));
        this.addWorkerThread(new ServerBaseTickThread(server, this));
        int max = 4;
        for (int i = 0; i < max; i++) {
            this.addWorkerThread(new ServerTickThread(server, this));
        }
    }

    private final Lock lock = new ReentrantLock();
    private final Condition doTick = lock.newCondition();
    private final Condition endTick = lock.newCondition();

    private int totalThreadCount = 0;
    private volatile int waitingOnThreads = 0;

    public void tick() {
        if (this.waitingOnThreads > 0) {
            throw new IllegalStateException("Attempted to tick server before ending previous tick!");
        }

        TickRate.INSTANCE.update();
        this.server.doPreTick();

        //wait on all threads
        this.waitingOnThreads = this.totalThreadCount;

        //notify all threads (since the previous tick is finished, they're all waiting on the dummy object)
        //this is where the actual tick starts doing things
        this.lock.lock();
        doTick.signalAll();
        this.lock.unlock();

        try {
            //wait for all worker threads to finish execution before proceeding to the next tick
            this.lock.lock();
            this.endTick.await();
        } catch (InterruptedException e) {
            this.server.getLogger().logException(e);
        } finally {
            this.lock.unlock();
        }

        this.server.doPostTick();
    }

    public void shutdown() {
        this.server.isRunning = false;

        //notify workers so that they stop waiting and then terminate as the server is flagged as not running
        this.lock.lock();
        this.endTick.signalAll();
        this.doTick.signalAll();
        this.lock.unlock();
    }

    public void onWorkerShutdown()  {
        this.server.logger.debug("Worker " + Thread.currentThread().getName() + " shutting down");
        this.totalThreadCount--;
        synchronized (this) {
            //decrement the waiting thread count to check if the tick is finished
            if (--this.waitingOnThreads <= 0) {
                this.lock.lock();
                this.endTick.signalAll();
                this.lock.unlock();
            }
        }
    }

    public void onWorkerFinish() {
        synchronized (this) {
            //decrement the waiting thread count to check if the tick is finished
            if (--this.waitingOnThreads <= 0) {
                this.lock.lock();
                this.endTick.signalAll();
                this.lock.unlock();
            }
        }

        workerWait();
    }

    public void workerWait() {
        try {
            this.lock.lock();
            this.doTick.await();
        } catch (InterruptedException e)    {
            this.server.getLogger().logException(e);
        } finally {
            this.lock.unlock();
        }
    }

    public void addWorkerThread(ServerExecutorThread thread) {
        if (thread.server == this.server && thread.tickManager == this) {
            totalThreadCount++;
        }
    }
}
