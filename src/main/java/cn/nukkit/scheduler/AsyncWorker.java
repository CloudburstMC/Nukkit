package cn.nukkit.scheduler;

import java.util.LinkedList;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class AsyncWorker extends Thread {
    private final LinkedList<AsyncTask> stack = new LinkedList<AsyncTask>();

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
                /*while (!this.stack.isEmpty()) {
                    AsyncTask task = stack.getFirst();
                    task.start();
                    stack.removeFirst();
                }*/
                for (AsyncTask task : stack) {
                    if (!task.isFinished()) {
                        task.run();
                    }
                }
            }
            try {
                sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
