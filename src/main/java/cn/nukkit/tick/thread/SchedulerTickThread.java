package cn.nukkit.tick.thread;

import cn.nukkit.Server;
import cn.nukkit.tick.ServerExecutorThread;
import cn.nukkit.tick.ServerTickManager;
import co.aikar.timings.Timings;

/**
 * @author DaPorkchop_
 *
 * Handles execution of scheduled tasks
 */
public class SchedulerTickThread extends ServerExecutorThread {
    public SchedulerTickThread(Server server, ServerTickManager tickManager) {
        super(server, tickManager);
    }

    @Override
    public void doRun() {
        Timings.schedulerTimer.startTiming();
        this.server.scheduler.mainThreadHeartbeat(this.server.tickCounter);
        Timings.schedulerTimer.stopTiming();
    }
}
