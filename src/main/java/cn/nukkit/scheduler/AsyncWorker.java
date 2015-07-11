package cn.nukkit.scheduler;

import java.util.LinkedList;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class AsyncWorker extends Thread {
    private LinkedList<AsyncTask> stack = new LinkedList<AsyncTask>();

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
            while (!this.stack.isEmpty()) {
                AsyncTask task = stack.getFirst();
                task.start();
                stack.removeFirst();
            }
            try {
                sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
