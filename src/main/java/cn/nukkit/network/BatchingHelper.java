package cn.nukkit.network;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.server.BatchPacketsEvent;
import cn.nukkit.network.protocol.DataPacket;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BatchingHelper {

    private final ExecutorService threadedExecutor;

    public BatchingHelper() {
        ThreadFactoryBuilder builder = new ThreadFactoryBuilder();
        builder.setNameFormat("Batching Executor");
        builder.setUncaughtExceptionHandler((thread, ex) -> Server.getInstance().getLogger().error("Exception in " + thread.getName(), ex));
        this.threadedExecutor = Executors.newSingleThreadExecutor(builder.build());
    }

    public void batchPackets(Server server, Player[] players, DataPacket[] packets) {
        if (players == null || packets == null || players.length == 0 || packets.length == 0) {
            return;
        }

        BatchPacketsEvent ev = new BatchPacketsEvent(players, packets, true);
        server.getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return;
        }

        this.threadedExecutor.execute(() -> { // Maybe players could have separate threads assigned to them?
            for (Player player : players) {
                for (DataPacket packet : packets) {
                    player.getNetworkSession().sendPacket(packet);
                }
            }
        });
    }

    public void shutdown() {
        this.threadedExecutor.shutdownNow();
    }
}
