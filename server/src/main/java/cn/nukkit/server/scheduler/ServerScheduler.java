package cn.nukkit.server.scheduler;

import cn.nukkit.api.plugin.Plugin;
import cn.nukkit.api.scheduler.NukkitRunnable;
import cn.nukkit.api.scheduler.NukkitScheduler;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.util.PluginException;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;

import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Nukkit Project Team
 */
@Log4j2
public class ServerScheduler implements NukkitScheduler {

    public static int WORKERS = 4;

    private final AsyncPool asyncPool;

    private final Queue<TaskHandler> pending;
    private final Queue<TaskHandler> queue;
    private final Map<Integer, TaskHandler> taskMap;
    private final AtomicInteger currentTaskId;

    private volatile int currentTick;

    public ServerScheduler(NukkitServer server) {
        this.pending = new ConcurrentLinkedQueue<>();
        this.currentTaskId = new AtomicInteger();
        this.queue = new PriorityQueue<>(11, (left, right) -> {
            int i = left.getNextRunTick() - right.getNextRunTick();
            if (i == 0) {
                return left.getTaskId() - right.getTaskId();
            }
            return i;
        });
        this.taskMap = new ConcurrentHashMap<>();
        this.asyncPool = new AsyncPool(server, WORKERS);
    }

    public AsyncPool getAsyncPool() {
        return asyncPool;
    }

    public TaskHandler scheduleTask(Plugin plugin, Runnable task) {
        return addTask(plugin, task, 0, 0, false);
    }

    public TaskHandler scheduleAsyncTask(Plugin plugin, Runnable task) {
        return addTask(plugin, task, 0, 0, true);
    }

    public int getAsyncTaskPoolSize() {
        return asyncPool.getPoolSize();
    }

    public void increaseAsyncTaskPoolSize(int newSize) {
        throw new UnsupportedOperationException("Cannot increase a working pool size."); //wtf?
    }

    public TaskHandler scheduleDelayedTask(Plugin plugin, Runnable task, int delay) {
        return addTask(plugin, task, delay, 0, false);
    }

    public TaskHandler scheduleRepeatingTask(Plugin plugin, Runnable task, int period) {
        return addTask(plugin, task, 0, period, false);
    }

    public TaskHandler scheduleDelayedRepeatingTask(Plugin plugin, Runnable task, int delay, int period) {
        return addTask(plugin, task, delay, period, false);
    }

    public void cancelTask(int taskId) {
        if (taskMap.containsKey(taskId)) {
            try {
                taskMap.remove(taskId).cancel();
            } catch (RuntimeException ex) {
                log.log(Level.ERROR, "Exception while invoking onCancel", ex);
            }
        }
    }

    public void cancelTasks(Plugin plugin) {
        if (plugin == null) {
            throw new NullPointerException("Plugin cannot be null!");
        }
        for (Map.Entry<Integer, TaskHandler> entry : taskMap.entrySet()) {
            TaskHandler taskHandler = entry.getValue();
            // TODO: Remove the "taskHandler.getPlugin() == null" check
            // It is only there for backwards compatibility!
            if (taskHandler.getPlugin() == null || plugin.equals(taskHandler.getPlugin())) {
                try {
                    taskHandler.cancel(); /* It will remove from task map automatic in next main heartbeat. */
                } catch (RuntimeException ex) {
                    log.log(Level.ERROR, "Exception while invoking onCancel", ex);
                }
            }
        }
    }

    public void cancelAllTasks() {
        for (Map.Entry<Integer, TaskHandler> entry : this.taskMap.entrySet()) {
            try {
                entry.getValue().cancel();
            } catch (RuntimeException ex) {
                log.log(Level.ERROR, "Exception while invoking onCancel", ex);
            }
        }

        this.taskMap.clear();
        this.queue.clear();
        this.currentTaskId.set(0);
    }

    public boolean isQueued(int taskId) {
        return this.taskMap.containsKey(taskId);
    }

    private TaskHandler addTask(Plugin plugin, Runnable task, int delay, int period, boolean asynchronous) {
        if (plugin != null && plugin.isDisabled()) {
            throw new PluginException("Plugin '" + plugin.getName() + "' attempted to register a task while disabled.");
        }
        if (delay < 0 || period < 0) {
            throw new PluginException("Attempted to register a task with negative delay or period.");
        }

        TaskHandler taskHandler = new TaskHandler(plugin, task, nextTaskId(), asynchronous);
        taskHandler.setDelay(delay);
        taskHandler.setPeriod(period);
        taskHandler.setNextRunTick(taskHandler.isDelayed() ? currentTick + taskHandler.getDelay() : currentTick);

        if (task instanceof NukkitRunnable) {
            ((NukkitRunnable) task).setHandler(taskHandler);
        }

        pending.offer(taskHandler);
        taskMap.put(taskHandler.getTaskId(), taskHandler);

        return taskHandler;
    }

    /*public void mainThreadHeartbeat(int currentTick) {
        this.currentTick = currentTick;
        // Accepts pending.
        while (!pending.isEmpty()) {
            queue.offer(pending.poll());
        }
        // Main heart beat.
        while (isReady(currentTick)) {
            TaskHandler taskHandler = queue.poll();
            if (taskHandler.isCancelled()) {
                
                taskMap.remove(taskHandler.getTaskId());
                continue;
            } else if (taskHandler.isAsync()) {
                asyncPool.execute(taskHandler.getTask());
            } else {
                taskHandler.timing.startTiming();
                try {
                    taskHandler.run(currentTick);
                } catch (Throwable e) {
                    log.log(Level.ERROR, "Could not execute taskHandler " + taskHandler.getTaskId() + ": " + e.getMessage(), e);
                }
                taskHandler.timing.stopTiming();
            }
            if (taskHandler.isRepeating()) {
                taskHandler.setNextRunTick(currentTick + taskHandler.getPeriod());
                pending.offer(taskHandler);
            } else {
                try {
                    Optional.ofNullable(taskMap.remove(taskHandler.getTaskId())).ifPresent(TaskHandler::cancel);
                } catch (RuntimeException ex) {
                    log.log(Level.ERROR, "Exception while invoking onCancel", ex);
                }
            }
        }
        AsyncTask.collectTask();
    }*/

    public int getQueueSize() {
        return queue.size() + pending.size();
    }

    private boolean isReady(int currentTick) {
        return this.queue.peek() != null && this.queue.peek().getNextRunTick() <= currentTick;
    }

    private int nextTaskId() {
        return currentTaskId.incrementAndGet();
    }

}
