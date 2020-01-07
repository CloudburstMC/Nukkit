package cn.nukkit.level.generator.function;

import cn.nukkit.level.Level;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.level.generator.SimpleChunkManager;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import lombok.extern.log4j.Log4j2;

import java.util.function.Function;

@Log4j2
public class ChunkGenerateFunction implements Function<Chunk, Chunk> {
    private static final LongSet generated = LongSets.synchronize(new LongOpenHashSet());

    private final Level level;

    public ChunkGenerateFunction(Level level) {
        this.level = level;
    }

    @Override
    public Chunk apply(Chunk chunk) {
        if (chunk == null || chunk.isGenerated()) {
            return chunk;
        }

        Generator generator = this.level.getGenerator();
        if (generator == null) {
            return chunk;
        }

        SimpleChunkManager manager = (SimpleChunkManager) generator.getChunkManager();
        if (manager == null) {
            return chunk;
        }

        if (!generated.add(Level.chunkKey(chunk.getX(), chunk.getZ()))) {
            log.debug("Already generated chunk ({}, {})", chunk.getX(), chunk.getZ());
        }

        manager.cleanChunks(this.level.getSeed());
        try {
            manager.setChunk(chunk);
            generator.generateChunk(chunk.getX(), chunk.getZ());
            chunk = manager.getChunk(chunk.getX(), chunk.getZ());
            chunk.setGenerated();
        } finally {
            chunk.setDirty(false);
            manager.cleanChunks(level.getSeed());
        }
        return chunk;
    }
}
