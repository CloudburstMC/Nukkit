package cn.nukkit.scheduler;

import cn.nukkit.InterruptibleThread;

import java.util.LinkedList;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class AsyncWorker extends Thread implements InterruptibleThread {
    private final LinkedList<AsyncTask> stack = new LinkedList<>();

    public AsyncWorker() {
        this.setName("Asynchronous Worker");
    }

    public void stack(AsyncTask task) {
        synchronized (stack) {
            stack.addFirst(task);
        }
    }

    public void unstack() {
        synchronized (stack) {
            stack.clear();
        }
    }

    public void unstack(AsyncTask task) {
        synchronized (stack) {
            stack.remove(task);
        }
    }

    public void run() {
        while (true) {
            synchronized (stack) {
                for (AsyncTask task : stack) {
                    if (!task.isFinished()) {
                        task.run();
                    }
                }
            }
            try {
                sleep(5);
            } catch (InterruptedException e) {
                //igonre
            }
        }
    }

}
