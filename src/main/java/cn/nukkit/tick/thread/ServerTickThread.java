package cn.nukkit.tick.thread;

import cn.nukkit.Server;
import cn.nukkit.tick.ServerExecutorThread;
import cn.nukkit.tick.ServerTickManager;

/**
 * @author DaPorkchop_
 *
 * A main worker thread that actually handles block updates and such
 */
public class ServerTickThread extends ServerExecutorThread {
    public ServerTickThread(Server server, ServerTickManager tickManager) {
        super(server, tickManager);
    }

    @Override
    public void doRun() {

    }
}
