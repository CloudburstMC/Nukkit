package cn.nukkit.tick.thread;

import cn.nukkit.Server;
import cn.nukkit.tick.ServerExecutorThread;
import cn.nukkit.tick.ServerTickManager;

/**
 * @author DaPorkchop_
 *
 * Automatically saves the world after a certain amount of time
 */
public class AutoSaveTickThread extends ServerExecutorThread {
    public AutoSaveTickThread(Server server, ServerTickManager tickManager) {
        super(server, tickManager, "Nukkit auto-save thread");
    }

    @Override
    public void doRun() {
        if (this.server.autoSave && ++this.server.autoSaveTicker >= this.server.autoSaveTicks) {
            this.server.autoSaveTicker = 0;
            this.server.doAutoSave();
        }

        if (this.server.sendUsageTicker > 0 && --this.server.sendUsageTicker == 0) {
            this.server.sendUsageTicker = 6000;
            //todo sendUsage
        }
    }
}
