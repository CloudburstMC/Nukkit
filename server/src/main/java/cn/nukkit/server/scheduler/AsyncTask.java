package cn.nukkit.server.scheduler;

import cn.nukkit.server.NukkitServer;
import co.aikar.timings.Timings;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Nukkit Project Team
 */

@Getter
@Log4j2
public final class AsyncTask {

    public static final Queue<AsyncTask> FINISHED_LIST = new ConcurrentLinkedQueue<>();

    @Setter
    private int taskId;
    private boolean finished = false;
    private Runnable runnable;

    public AsyncTask(Runnable runnable) {
        this.runnable = runnable;
    }

    public void run() {
        this.runnable.run();
        this.finished = true;
        FINISHED_LIST.offer(this);
    }

    public static void collectTask() {
        Timings.schedulerAsyncTimer.startTiming();
        while (!FINISHED_LIST.isEmpty()) {
            AsyncTask task = FINISHED_LIST.poll();
            try {
                Runnable runnable = task.getRunnable();
                if (runnable instanceof cn.nukkit.api.scheduler.AsyncTask) {
                    ((cn.nukkit.api.scheduler.AsyncTask) runnable).onCompletion(NukkitServer.getInstance());
                }
            } catch (Exception e) {
                log.log(Level.ERROR, "Exception while async task "
                        + task.getTaskId()
                        + " invoking onCompletion", e);
            }
        }
        Timings.schedulerAsyncTimer.stopTiming();
    }

}
