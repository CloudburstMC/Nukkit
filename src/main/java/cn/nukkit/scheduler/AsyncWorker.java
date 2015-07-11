package cn.nukkit.scheduler;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class AsyncWorker extends Thread {
    private final LinkedList<AsyncTask> stack = new LinkedList<AsyncTask>();

    synchronized public void stack(AsyncTask task) {
        stack.addFirst(task);
    }

    synchronized public void unstack() {
        stack.clear();
    }

    synchronized public void unstack(AsyncTask task) {
        stack.remove(task);
    }

    public void run() {
        while (true) {
            while (!stack.isEmpty()) {
                AsyncTask task = stack.getFirst();
                task.start();
                stack.removeFirst();
            }
        }
    }

}
