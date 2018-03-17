package cn.nukkit.tick.thread;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.server.QueryRegenerateEvent;
import cn.nukkit.tick.ServerExecutorThread;
import cn.nukkit.tick.ServerTickManager;
import co.aikar.timings.Timings;

import java.util.ArrayList;

/**
 * @author DaPorkchop_
 *
 * Handles all networking updates and packet handlers
 */
public class NetworkingTickThread extends ServerExecutorThread {
    public NetworkingTickThread(Server server, ServerTickManager tickManager)   {
        super(server, tickManager, "Nukkit networking update thread");
    }

    @Override
    public void doRun() {
        Timings.connectionTimer.startTiming();
        this.server.network.processInterfaces();

        if (this.server.rcon != null) {
            this.server.rcon.check();
        }
        Timings.connectionTimer.stopTiming();

        for (Player player : new ArrayList<>(this.server.players.values())) {
            player.checkNetwork();
        }

        if ((this.server.tickCounter & 0b1111) == 0) {
            this.server.titleTick();
            this.server.network.resetStatistics();

            if ((this.server.tickCounter & 0b111111111) == 0) {
                try {
                    this.server.getPluginManager().callEvent(this.server.queryRegenerateEvent = new QueryRegenerateEvent(this.server, 5));
                    if (this.server.queryHandler != null) {
                        this.server.queryHandler.regenerateInfo();
                    }
                } catch (Exception e) {
                    this.server.logger.logException(e);
                }
            }

            this.server.network.updateName();
        }
    }
}
