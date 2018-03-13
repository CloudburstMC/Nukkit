package cn.nukkit.tick.thread;

import cn.nukkit.Player;
import cn.nukkit.Server;
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
        super(server, tickManager);
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
    }
}
