package cn.nukkit.scheduler;

import cn.nukkit.Server;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.utils.PluginException;

import java.util.*;

/**
 * @author Nukkit Project Team
 */
public class ServerScheduler {

    public static int WORKERS = 4;

    private final AsyncPool asyncPool;

    private final Queue<TaskHandler> queue;
    private final Map<Integer, TaskHandler> tasks;

    private int currentTaskId;
    private int currentTick;

    public ServerScheduler() {
        this.queue = new PriorityQueue<>(11, (left, right) -> left.getNextRunTick() - right.getNextRunTick());
        this.tasks = new HashMap<>();
        this.asyncPool = new AsyncPool(Server.getInstance(), WORKERS);
    }

    @Deprecated
    public TaskHandler scheduleTask(Task task) {
        return addTask(task, 0, 0, false);
    }

    public TaskHandler scheduleTask(Runnable task) {
        return addTask(null, task, 0, 0, false);
    }

    public TaskHandler scheduleTask(Runnable task, boolean asynchronous) {
        return addTask(null, task, 0, 0, asynchronous);
    }

    @Deprecated
    public TaskHandler scheduleAsyncTask(AsyncTask task) {
        return addTask(null, task, 0, 0, true);
    }

    @Deprecated
    public void scheduleAsyncTaskToWorker(AsyncTask task, int worker) {
        scheduleAsyncTask(task);
    }

    public int getAsyncTaskPoolSize() {
        return asyncPool.getSize();
    }

    public void increaseAsyncTaskPoolSize(int newSize) {
        throw new UnsupportedOperationException("Cannot increase a working pool size.");
    }

    public TaskHandler scheduleDelayedTask(Task task, int delay) {
        return this.addTask(task, delay, 0, false);
    }

    public TaskHandler scheduleDelayedTask(Task task, int delay, boolean asynchronous) {
        return this.addTask(task, delay, 0, asynchronous);
    }

    public TaskHandler scheduleDelayedTask(Runnable task, int delay) {
        return addTask(null, task, delay, 0, false);
    }

    public TaskHandler scheduleDelayedTask(Runnable task, int delay, boolean asynchronous) {
        return addTask(null, task, delay, 0, asynchronous);
    }

    public TaskHandler scheduleRepeatingTask(Runnable task, int period) {
        return addTask(null, task, 0, period, false);
    }

    public TaskHandler scheduleRepeatingTask(Runnable task, int period, boolean asynchronous) {
        return addTask(null, task, 0, period, asynchronous);
    }

    public TaskHandler scheduleRepeatingTask(Task task, int period) {
        return addTask(task, 0, period, false);
    }

    public TaskHandler scheduleRepeatingTask(Task task, int period, boolean asynchronous) {
        return addTask(task, 0, period, asynchronous);
    }

    public TaskHandler scheduleDelayedRepeatingTask(Task task, int delay, int period) {
        return addTask(task, delay, period, false);
    }

    public TaskHandler scheduleDelayedRepeatingTask(Task task, int delay, int period, boolean asynchronous) {
        return addTask(task, delay, period, asynchronous);
    }

    public TaskHandler scheduleDelayedRepeatingTask(Runnable task, int delay, int period) {
        return addTask(null, task, delay, period, false);
    }

    public TaskHandler scheduleDelayedRepeatingTask(Runnable task, int delay, int period, boolean asynchronous) {
        return addTask(null, task, delay, period, asynchronous);
    }

    public void cancelTask(int taskId) {
        if (tasks.containsKey(taskId)) {
            tasks.remove(taskId).cancel();
        }
    }

    public void cancelTask(Plugin plugin) {
        if (plugin == null) {
            throw new NullPointerException("Plugin cannot be null!");
        }
        for (Map.Entry<Integer, TaskHandler> entry : tasks.entrySet()) {
            TaskHandler taskHandler = entry.getValue();
            if (plugin.equals(taskHandler.getPlugin())) {
                taskHandler.cancel(); /* It will remove from task map automatic in next main heartbeat. */
            }
        }
    }

    public void cancelAllTasks() {
        for (Map.Entry<Integer, TaskHandler> entry : this.tasks.entrySet()) {
            entry.getValue().cancel();
        }
        this.tasks.clear();
        this.queue.clear();
        this.currentTaskId = 0;
    }

    public boolean isQueued(int taskId) {
        return this.tasks.containsKey(taskId);
    }

    private TaskHandler addTask(Task task, int delay, int period, boolean asynchronous) {
        return addTask(task instanceof PluginTask ? ((PluginTask) task).getOwner() : null, () -> task.onRun(currentTick + delay), delay, period, asynchronous);
    }

    private TaskHandler addTask(Plugin plugin, Runnable task, int delay, int period, boolean asynchronous) {
        if (plugin != null && plugin.isDisabled()) {
            throw new PluginException("Plugin '" + plugin.getName() + "' attempted to register a task while disabled.");
        }
        if (delay < 0 || period < 0) {
            throw new PluginException("Attempted to register a task with negative delay or period.");
        }

        TaskHandler taskHandler = new TaskHandler(plugin, "Unknown", task, nextTaskId(), asynchronous);
        taskHandler.setDelay(delay);
        taskHandler.setPeriod(period);

        return handle(taskHandler);
    }

    private TaskHandler handle(TaskHandler handler) {
        int nextRun;
        if (handler.isDelayed()) {
            nextRun = this.currentTick + handler.getDelay();
        } else {
            nextRun = this.currentTick;
        }
        handler.setNextRunTick(nextRun);

        this.tasks.put(handler.getTaskId(), handler);
        this.queue.add(handler);

        return handler;
    }

    public void mainThreadHeartbeat(int currentTick) {
        this.currentTick = currentTick;
        while (isReady(currentTick)) {
            TaskHandler taskHandler = queue.poll();
            if (taskHandler.isCancelled()) {
                tasks.remove(taskHandler.getTaskId());
                continue;
            } else if (taskHandler.isAsynchronous()) {
                asyncPool.submitTask(taskHandler.getTask());
            } else {
                try {
                    taskHandler.run(currentTick);
                } catch (Exception e) {
                    Server.getInstance().getLogger().critical("Could not execute taskHandler " + taskHandler.getTaskName() + ": " + e.getMessage());
                    Server.getInstance().getLogger().logException(e);
                }
            }
            if (taskHandler.isRepeating()) {
                taskHandler.setNextRunTick(currentTick + taskHandler.getPeriod());
                this.queue.offer(taskHandler);
            } else {
                taskHandler.cancel();
                this.tasks.remove(taskHandler.getTaskId());
            }
        }
        AsyncTask.collectTask();
    }

    public int getQueueSize() {
        return queue.size();
    }

    private boolean isReady(int currentTick) {
        return this.queue.peek() != null && this.queue.peek().getNextRunTick() <= currentTick;
    }

    private int nextTaskId() {
        return ++currentTaskId;
    }

}
