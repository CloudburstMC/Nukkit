package cn.nukkit.scheduler;

import java.util.LinkedList;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class AsyncWorker extends Thread {
    private final LinkedList<AsyncTask> stack = new LinkedList<AsyncTask>();

    public void stack(AsyncTask task) {
        stack.addFirst(task);
        this.run();
    }

    public void unstack() {
        stack.clear();
    }

    public void unstack(AsyncTask task) {
        stack.remove(task);
    }

    public void run() {
        synchronized (stack){
            while (!stack.isEmpty()) {
                AsyncTask task = stack.getFirst();
                task.start();
                stack.removeFirst();
            }
        }
    }

}
