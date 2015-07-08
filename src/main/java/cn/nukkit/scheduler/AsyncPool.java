package cn.nukkit.scheduler;

import cn.nukkit.Server;
import com.sun.javaws.exceptions.InvalidArgumentException;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class AsyncPool {
    private Server server;

    protected int size;

    private Map<Integer, AsyncTask> tasks = new HashMap<Integer, AsyncTask>();

    private Map<Integer, Integer> taskWorkers = new HashMap<Integer, Integer>();

    private AsyncWorker[] workers;

    private int[] workerUsage;

    public AsyncPool(Server server, int size) {
        this.server = server;
        this.size = size;
        this.workerUsage = new int[size];
        this.workers = new AsyncWorker[size];
        for (int i = 0; i < this.size; ++i) {
            this.workerUsage[i] = 0;
            this.workers[i] = new AsyncWorker();
            this.workers[i].start();
        }
    }

    public int getSize() {
        return size;
    }

    public void increaseSize(int newSize) {
        if (newSize > this.size) {
            for (int i = this.size; i < newSize; ++i) {
                this.workerUsage[i] = 0;
                this.workers[i] = new AsyncWorker();
                this.workers[i].start();
            }
            this.size = newSize;
        }
    }

    public void submitTaskToWorker(AsyncTask task, int worker) throws InvalidArgumentException {
        if (this.tasks.containsKey(task.getTaskId()) || task.isFinished()) {
            return;
        }
        if (worker < 0 || worker >= this.size) {
            throw new InvalidArgumentException(new String[]{"不可用的 worker " + worker});
        }
        this.tasks.put(task.getTaskId(), task);
        this.workers[worker].stack(task);
        this.workerUsage[worker]++;
        this.taskWorkers.put(task.getTaskId(), worker);
    }

    public void submitTask(AsyncTask task) {
        if (this.tasks.containsKey(task.getTaskId()) || task.isFinished()) {
            return;
        }
        int selectedWorker = new Random().nextInt(this.size - 1);
        int selectedTasks = this.workerUsage[selectedWorker];
        for (int i = 0; i < this.size; ++i) {
            if (this.workerUsage[i] < selectedTasks) {
                selectedWorker = i;
                selectedTasks = this.workerUsage[i];
            }
        }
        try {
            this.submitTaskToWorker(task, selectedWorker);
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        }
    }

    private void removeTask(AsyncTask task) {
        this.removeTask(task, false);
    }

    private void removeTask(AsyncTask task, boolean force) {
        if (this.taskWorkers.containsKey(task.getTaskId())) {
            if (!force && (task.isAlive() || !task.isFinished())) {
                return;
            }
            int w = this.taskWorkers.get(task.getTaskId());
            this.workers[w].unstack(task);
            this.workerUsage[w]--;
        }
        this.tasks.remove(task.getTaskId());
        this.tasks.remove(task.getTaskId());

        task.cleanObject();
    }

    public void removeTasks() {
        do {
            for (Map.Entry entry : this.tasks.entrySet()) {
                this.removeTask((AsyncTask) entry.getValue());
            }
            if (this.tasks.size() > 0) {
                try {
                    Thread.sleep(25);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } while (this.tasks.size() > 0);
        this.tasks = new HashMap<Integer, AsyncTask>();
        this.taskWorkers = new HashMap<Integer, Integer>();
    }

    public void collectTasks() {
        for (Map.Entry entry : this.tasks.entrySet()) {
            AsyncTask task = (AsyncTask) entry.getValue();
            if (task.isFinished() && !task.isAlive()) {
                task.onCompletion(this.server);
                this.removeTask(task);
            } else if (task.isInterrupted()) {
                this.removeTask(task, true);
            }
        }
    }
}
