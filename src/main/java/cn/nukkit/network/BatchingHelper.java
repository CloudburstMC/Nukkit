package cn.nukkit.network;

import cn.nukkit.Player;
import cn.nukkit.Server;
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

    public void batchPackets(Player[] players, DataPacket[] packets) {
        if (players.length > 0 && packets.length > 0) {
            this.threadedExecutor.execute(() -> {
                for (Player player : players) {
                    for (DataPacket packet : packets) {
                        player.getNetworkSession().sendPacket(packet);
                    }
                }
            });
        }
    }

    public void shutdown() {
        this.threadedExecutor.shutdownNow();
    }
}
