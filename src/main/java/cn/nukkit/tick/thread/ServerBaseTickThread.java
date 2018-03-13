package cn.nukkit.tick.thread;

import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.tick.ServerExecutorThread;
import cn.nukkit.tick.ServerTickManager;

/**
 * @author DaPorkchop_
 *
 * Does any actions on the server and levels that are too short and trivial to bother being done by multiple workers
 * e.g. incrementing time/weather counters, etc.
 */
public class ServerBaseTickThread extends ServerExecutorThread {
    public ServerBaseTickThread(Server server, ServerTickManager tickManager) {
        super(server, tickManager);
    }

    @Override
    public void doRun() {
        for (Level level : this.server.levelArray)  {
            level.doBaseTick(this.server.tickCounter);
        }
    }
}
