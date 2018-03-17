package cn.nukkit.scheduler;

import cn.nukkit.Server;
import cn.nukkit.utils.ThreadStore;
import co.aikar.timings.Timings;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AsyncTask implements Runnable {

    public static final Queue<AsyncTask> FINISHED_LIST = new ConcurrentLinkedQueue<>();

    private Object result;
    private int taskId;
    private boolean finished = false;

    public void run() {
        this.result = null;
        this.onRun();
        this.finished = true;
        FINISHED_LIST.offer(this);
    }

    public boolean isFinished() {
        return this.finished;
    }

    public Object getResult() {
        return this.result;
    }

    public boolean hasResult() {
        return this.result != null;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getTaskId() {
        return this.taskId;
    }

    public Object getFromThreadStore(String identifier) {
        return this.isFinished() ? null : ThreadStore.store.get(identifier);
    }

    public void saveToThreadStore(String identifier, Object value) {
        if (!this.isFinished()) {
            if (value == null) {
                ThreadStore.store.remove(identifier);
            } else {
                ThreadStore.store.put(identifier, value);
            }
        }
    }

    public abstract void onRun();

    public void onCompletion(Server server) {

    }

    public void cleanObject() {
        this.result = null;
        this.taskId = 0;
        this.finished = false;
    }

    private static Lock collectionLock = new ReentrantLock();

    public static void threadedCollectTask() {
        collectionLock.lock();
        while (!FINISHED_LIST.isEmpty()) {
            AsyncTask task = FINISHED_LIST.poll();
            collectionLock.unlock();
            try {
                task.onCompletion(Server.getInstance());
            } catch (Exception e) {
                Server.getInstance().getLogger().critical("Exception while async task "
                        + task.getTaskId()
                        + " invoking onCompletion", e);
            }
            collectionLock.lock();
        }
        collectionLock.unlock();
    }
}
