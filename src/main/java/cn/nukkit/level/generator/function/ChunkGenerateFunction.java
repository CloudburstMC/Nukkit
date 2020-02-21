package cn.nukkit.level.generator.function;

import cn.nukkit.level.Level;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.level.chunk.LockableChunk;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.math.BedrockRandom;
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

        LockableChunk lockableChunk = chunk.writeLockable();

        BedrockRandom random = BedrockRandom.getThreadLocal();
        long seed = Generator.getChunkSeed(chunk.getX(), chunk.getZ(), this.level.getSeed());
        random.setSeed((int) seed);

        lockableChunk.lock();
        try {
            generator.generateChunk(random, chunk);
            chunk.setGenerated();
        } finally {
            lockableChunk.unlock();
        }
        return chunk;
    }
}
