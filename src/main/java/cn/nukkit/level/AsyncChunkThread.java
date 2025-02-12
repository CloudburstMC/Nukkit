package cn.nukkit.level;

import cn.nukkit.level.format.generic.BaseChunk;
import cn.nukkit.Server;
import cn.nukkit.level.format.generic.serializer.NetworkChunkData;
import cn.nukkit.level.format.generic.serializer.NetworkChunkSerializer;
import cn.nukkit.utils.BinaryStream;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

class AsyncChunkThread {

    private final ExecutorService threadedExecutor;
    final Queue<AsyncChunkData> out = new ConcurrentLinkedQueue<>();

    AsyncChunkThread(String levelName) {
        ThreadFactoryBuilder builder = new ThreadFactoryBuilder();
        builder.setNameFormat("AsyncChunkThread for " + levelName);
        builder.setUncaughtExceptionHandler((thread, ex) -> Server.getInstance().getLogger().error("Exception in " + thread.getName(), ex));
        this.threadedExecutor = Executors.newSingleThreadExecutor(builder.build());
    }

    void queue(BaseChunk chunk, long timestamp, int x, int z, DimensionData dimensionData) {
        this.threadedExecutor.execute(() -> this.run(chunk, timestamp, x, z, dimensionData));
    }

    private void run(BaseChunk chunk, long timestamp, int x, int z, DimensionData dimensionData) {
        BiConsumer<BinaryStream, NetworkChunkData> callback = (stream, data) ->
                this.out.add(new AsyncChunkData(timestamp, x, z, Level.chunkHash(x, z), stream.getBuffer(), data.getChunkSections()));
        NetworkChunkSerializer.serialize(chunk, callback, dimensionData);
    }

    void shutdown() {
        this.threadedExecutor.shutdownNow();
    }
}
