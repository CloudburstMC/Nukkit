package cn.nukkit.scheduler;

import java.util.LinkedList;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class AsyncWorker extends Thread {
    private LinkedList<AsyncTask> stack = new LinkedList<AsyncTask>();

    public void stack(AsyncTask task) {
        stack.add(task);
        task.run();
    }

    public void unstack() {
        stack.clear();
    }

    public void unstack(AsyncTask task) {
        stack.remove(task);
    }

}
