package cn.nukkit.level.generator.function;

import cn.nukkit.level.Level;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.level.generator.SimpleChunkManager;
import com.google.common.base.Preconditions;

import java.util.List;
import java.util.function.BiFunction;

public class ChunkPopulateFunction implements BiFunction<Chunk, List<Chunk>, Chunk> {
    private final Level level;

    public ChunkPopulateFunction(Level level) {
        this.level = level;
    }

    @Override
    public Chunk apply(Chunk chunk, List<Chunk> chunks) {
        if (chunk == null || !chunk.isGenerated() || chunk.isPopulated()) {
            return chunk;
        }

        Generator generator = level.getGenerator();
        if (generator == null) {
            return chunk;
        }

        SimpleChunkManager manager = (SimpleChunkManager) generator.getChunkManager();
        if (manager == null) {
            return chunk;
        }

        manager.cleanChunks(this.level.getSeed());
        try {
            manager.setChunk(chunk);

            for (Chunk aroundChunk : chunks) {
                Preconditions.checkNotNull(aroundChunk, "aroundChunk");
                manager.setChunk(aroundChunk);
                if (!aroundChunk.isGenerated()) {
                    throw new IllegalStateException("Chunk should have been loaded before");
                }
            }

            if (!chunk.isPopulated()) {
                generator.populateChunk(chunk.getX(), chunk.getZ());
                //chunk = manager.getChunk(chunk.getX(), chunk.getZ());
                chunk.setPopulated();
                chunk.recalculateHeightMap();
                chunk.populateSkyLight();
                //chunk.setLightPopulated();
            }

            manager.setChunk(chunk);
        } finally {
            manager.cleanChunks(this.level.getSeed());
        }

        return chunk;
    }
}
