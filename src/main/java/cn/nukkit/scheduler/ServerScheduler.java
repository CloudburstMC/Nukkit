package cn.nukkit.scheduler;

import cn.nukkit.Server;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.utils.PluginException;
import cn.nukkit.utils.Utils;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.ArrayDeque;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Nukkit Project Team
 */
public class ServerScheduler {
    private static final ArrayDeque<TaskHandler> EMPTY_QUEUE = new ArrayDeque<TaskHandler>() {
        @Override
        public boolean isEmpty() {
            return true;
        }
    };

    private final Queue<TaskHandler> pending;
    private final Int2ObjectMap<ArrayDeque<TaskHandler>> queueMap;
    private final Int2ObjectMap<TaskHandler> taskMap;
    private final AtomicInteger currentTaskId;

    private final ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), new ThreadFactoryBuilder().setDaemon(true).setNameFormat("Nukkit Asynchronous Task Handler #%d").build());

    private volatile int currentTick = -1;

    public ServerScheduler() {
        this.pending = new ConcurrentLinkedQueue<>();
        this.currentTaskId = new AtomicInteger();
        this.queueMap = Int2ObjectMaps.synchronize(new Int2ObjectOpenHashMap<>());
        this.taskMap = Int2ObjectMaps.synchronize(new Int2ObjectOpenHashMap<>());
    }

    public TaskHandler scheduleTask(Task task) {
        return addTask(task, 0, 0, false);
    }

    /**
     * @deprecated Use {@link #scheduleTask(Plugin, Runnable)
     */
    @Deprecated
    public TaskHandler scheduleTask(Runnable task) {
        return addTask(null, task, 0, 0, false);
    }

    public TaskHandler scheduleTask(Plugin plugin, Runnable task) {
        return addTask(plugin, task, 0, 0, false);
    }

    /**
     * @deprecated Use {@link #scheduleTask(Plugin, Runnable, boolean)
     */
    @Deprecated
    public TaskHandler scheduleTask(Runnable task, boolean asynchronous) {
        return addTask(null, task, 0, 0, asynchronous);
    }

    public TaskHandler scheduleTask(Plugin plugin, Runnable task, boolean asynchronous) {
        return addTask(plugin, task, 0, 0, asynchronous);
    }

    public TaskHandler scheduleDelayedTask(Task task, int delay) {
        return this.addTask(task, delay, 0, false);
    }

    public TaskHandler scheduleDelayedTask(Task task, int delay, boolean asynchronous) {
        return this.addTask(task, delay, 0, asynchronous);
    }

    /**
     * @deprecated Use {@link #scheduleDelayedTask(Plugin, Runnable, int)
     */
    @Deprecated
    public TaskHandler scheduleDelayedTask(Runnable task, int delay) {
        return addTask(null, task, delay, 0, false);
    }

    public TaskHandler scheduleDelayedTask(Plugin plugin, Runnable task, int delay) {
        return addTask(plugin, task, delay, 0, false);
    }

    /**
     * @deprecated Use {@link #scheduleDelayedTask(Plugin, Runnable, int, boolean)
     */
    @Deprecated
    public TaskHandler scheduleDelayedTask(Runnable task, int delay, boolean asynchronous) {
        return addTask(null, task, delay, 0, asynchronous);
    }

    public TaskHandler scheduleDelayedTask(Plugin plugin, Runnable task, int delay, boolean asynchronous) {
        return addTask(plugin, task, delay, 0, asynchronous);
    }

    /**
     * @deprecated Use {@link #scheduleRepeatingTask(Plugin, Runnable, int)
     */
    @Deprecated
    public TaskHandler scheduleRepeatingTask(Runnable task, int period) {
        return addTask(null, task, 0, period, false);
    }

    public TaskHandler scheduleRepeatingTask(Plugin plugin, Runnable task, int period) {
        return addTask(plugin, task, 0, period, false);
    }

    /**
     * @deprecated Use {@link #scheduleRepeatingTask(Plugin, Runnable, int, boolean)
     */
    @Deprecated
    public TaskHandler scheduleRepeatingTask(Runnable task, int period, boolean asynchronous) {
        return addTask(null, task, 0, period, asynchronous);
    }

    public TaskHandler scheduleRepeatingTask(Plugin plugin, Runnable task, int period, boolean asynchronous) {
        return addTask(plugin, task, 0, period, asynchronous);
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

    /**
     * @deprecated Use {@link #scheduleDelayedRepeatingTask(Plugin, Runnable, int, int)
     */
    @Deprecated
    public TaskHandler scheduleDelayedRepeatingTask(Runnable task, int delay, int period) {
        return addTask(null, task, delay, period, false);
    }

    public TaskHandler scheduleDelayedRepeatingTask(Plugin plugin, Runnable task, int delay, int period) {
        return addTask(plugin, task, delay, period, false);
    }

    /**
     * @deprecated Use {@link #scheduleDelayedRepeatingTask(Plugin, Runnable, int, int, boolean)
     */
    @Deprecated
    public TaskHandler scheduleDelayedRepeatingTask(Runnable task, int delay, int period, boolean asynchronous) {
        return addTask(null, task, delay, period, asynchronous);
    }

    public TaskHandler scheduleDelayedRepeatingTask(Plugin plugin, Runnable task, int delay, int period, boolean asynchronous) {
        return addTask(plugin, task, delay, period, asynchronous);
    }

    public void cancelTask(int taskId) {
        if (taskMap.containsKey(taskId)) {
            try {
                taskMap.remove(taskId).cancel();
            } catch (RuntimeException ex) {
                Server.getInstance().getLogger().critical("Exception while invoking onCancel", ex);
            }
        }
    }

    public void cancelTask(Plugin plugin) {
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
                    Server.getInstance().getLogger().critical("Exception while invoking onCancel", ex);
                }
            }
        }
    }

    public void cancelAllTasks() {
        for (Map.Entry<Integer, TaskHandler> entry : this.taskMap.entrySet()) {
            try {
                entry.getValue().cancel();
            } catch (RuntimeException ex) {
                Server.getInstance().getLogger().critical("Exception while invoking onCancel", ex);
            }
        }
        this.taskMap.clear();
        this.queueMap.clear();
        this.currentTaskId.set(0);
        this.service.shutdownNow();
    }

    public boolean isQueued(int taskId) {
        return this.taskMap.containsKey(taskId);
    }

    private TaskHandler addTask(Task task, int delay, int period, boolean asynchronous) {
        return addTask(task instanceof PluginTask ? ((PluginTask) task).getOwner() : null, task, delay, period, asynchronous);
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

        if (task instanceof Task) {
            ((Task) task).setHandler(taskHandler);
        }

        pending.offer(taskHandler);
        taskMap.put(taskHandler.getTaskId(), taskHandler);

        return taskHandler;
    }

    public TaskHandler scheduleAsyncTask(AsyncTask task) {
        return addTask(null, task, 0, 0, true);
    }

    public TaskHandler scheduleAsyncTask(AsyncTask task, int delay) {
        return addTask(null, task, delay, 0, true);
    }

    public TaskHandler scheduleAsyncTask(AsyncTask task, int delay, int period) {
        return addTask(null, task, delay, period, true);
    }

    public TaskHandler forceRunAsync(AsyncTask task) {
        TaskHandler handler = new TaskHandler(null, task, nextTaskId(), true);
        service.submit(() -> {
            try {
                handler.run(currentTick);
            } catch (Throwable e) {
                Server.getInstance().getLogger().critical("Could not execute async taskHandler " + handler.getTaskId() + ": " + e.getMessage());
                Server.getInstance().getLogger().logException(e instanceof Exception ? (Exception) e : new RuntimeException(e));
            }
        });
        return handler;
    }

    private final Lock heartbeatLock = new ReentrantLock();
    private volatile ArrayDeque<TaskHandler> currentQueue;

    public void threadedHeartbeat(int currentTick) {
        TaskHandler task;
        //add pending tasks on single thread
        heartbeatLock.lock();
        while ((task = pending.poll()) != null) {
            int tick = Math.max(currentTick, task.getNextRunTick()); // Do not schedule in the past
            ArrayDeque<TaskHandler> queue = Utils.getOrCreate(queueMap, ArrayDeque.class, tick);
            queue.add(task);
        }
        //execute tasks on multiple threads
        if (currentQueue == null) {
            currentQueue = queueMap.remove(currentTick);
            if (currentQueue == null) {
                currentQueue = EMPTY_QUEUE;
            }
        }
        while (!currentQueue.isEmpty()) {
            TaskHandler handler = currentQueue.poll();
            heartbeatLock.unlock();
            if (handler.isCancelled()) {
                taskMap.remove(handler.getTaskId());
            } else if (handler.async) {
                service.submit(() -> {
                    try {
                        handler.run(currentTick);
                    } catch (Throwable e) {
                        Server.getInstance().getLogger().critical("Could not execute async taskHandler " + handler.getTaskId() + ": " + e.getMessage());
                        Server.getInstance().getLogger().logException(e instanceof Exception ? (Exception) e : new RuntimeException(e));
                    }
                });
            } else {
                try {
                    handler.run(currentTick);
                } catch (Throwable e) {
                    Server.getInstance().getLogger().critical("Could not execute taskHandler " + handler.getTaskId() + ": " + e.getMessage());
                    Server.getInstance().getLogger().logException(e instanceof Exception ? (Exception) e : new RuntimeException(e));
                }
            }
            if (handler.isRepeating()) {
                handler.setNextRunTick(currentTick + handler.getPeriod());
                pending.offer(handler);
            } else {
                try {
                    TaskHandler removed = taskMap.remove(handler.getTaskId());
                    if (removed != null) removed.cancel();
                } catch (RuntimeException ex) {
                    Server.getInstance().getLogger().critical("Exception while invoking onCancel", ex);
                }
            }
            heartbeatLock.lock();
        }

        this.currentTick = currentTick;
        heartbeatLock.unlock();
        AsyncTask.threadedCollectTask();
    }

    public int getQueueSize() {
        int size = pending.size();
        for (ArrayDeque<TaskHandler> queue : queueMap.values()) {
            size += queue.size();
        }
        return size;
    }

    private int nextTaskId() {
        return currentTaskId.incrementAndGet();
    }

    public void doPostTick() {
        currentQueue = null;
    }
}
