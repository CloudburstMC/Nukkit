package cn.nukkit.scheduler;

import java.util.LinkedList;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class AsyncWorker extends cn.nukkit.Thread {
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
                /*while (!this.stack.isEmpty()) {
                    AsyncTask task = stack.getFirst();
                    task.start();
                    stack.removeFirst();
                }*/
                stack.stream().filter(task -> !task.isFinished()).forEach(cn.nukkit.scheduler.AsyncTask::run);
            }
            try {
                sleep(5);
            } catch (InterruptedException e) {
                //igonre
            }
        }
    }

}
