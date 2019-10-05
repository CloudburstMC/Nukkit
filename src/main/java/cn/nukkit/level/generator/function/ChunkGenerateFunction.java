package cn.nukkit.level.generator.function;

import cn.nukkit.level.Level;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.level.generator.SimpleChunkManager;

import java.util.function.Function;

public class ChunkGenerateFunction implements Function<Chunk, Chunk> {
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

        manager.cleanChunks(this.level.getSeed());
        try {
            manager.setChunk(chunk);
            generator.generateChunk(chunk.getX(), chunk.getZ());
            chunk = manager.getChunk(chunk.getX(), chunk.getZ());
            chunk.setGenerated();
        } finally {
            manager.cleanChunks(level.getSeed());
        }
        return chunk;
    }
}
