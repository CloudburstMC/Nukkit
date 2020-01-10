package cn.nukkit.level.generator.function;

import cn.nukkit.level.Level;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.level.chunk.LockableChunk;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.level.generator.SimpleChunkManager;
import lombok.extern.log4j.Log4j2;

import java.util.function.Function;

@Log4j2
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

        LockableChunk lockableChunk = chunk.lockable();

        manager.setSeed(this.level.getSeed());
        manager.setChunk(lockableChunk);
        lockableChunk.lock();
        try {
            generator.generateChunk(chunk.getX(), chunk.getZ());
            chunk.setGenerated();
        } finally {
            chunk.setDirty(false);
            manager.clean();
            lockableChunk.unlock();
        }
        return chunk;
    }
}
