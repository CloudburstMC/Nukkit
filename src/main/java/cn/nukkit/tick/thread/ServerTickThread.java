package cn.nukkit.tick.thread;

import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.tick.ServerExecutorThread;
import cn.nukkit.tick.ServerTickManager;

/**
 * @author DaPorkchop_
 *
 * A main worker thread that actually handles block updates and such
 * This thread should only handle a fragment of the actual world
 */
public class ServerTickThread extends ServerExecutorThread {
    private static int count = 0;

    public ServerTickThread(Server server, ServerTickManager tickManager) {
        super(server, tickManager, "Nukkit tick thread #" + (++count));
    }

    @Override
    public void doRun() {
        this.server.threadedTick();
    }
}
