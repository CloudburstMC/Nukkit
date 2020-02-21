package cn.nukkit.level.generator.function;

import cn.nukkit.level.Level;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.level.chunk.LockableChunk;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.level.generator.PopChunkManager;
import cn.nukkit.math.BedrockRandom;
import com.google.common.base.Preconditions;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Log4j2
public class ChunkPopulateFunction implements BiFunction<Chunk, List<Chunk>, Chunk> {

    private static final ThreadLocal<PopChunkManager> POP_CHUNK_MANAGER = ThreadLocal.withInitial(PopChunkManager::new);

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

        PopChunkManager manager = POP_CHUNK_MANAGER.get();

        BedrockRandom random = BedrockRandom.getThreadLocal();
        long seed = Generator.getChunkSeed(chunk.getX(), chunk.getZ(), this.level.getSeed());
        random.setSeed((int) seed);

        manager.setSeed(this.level.getSeed());

        chunks.add(chunk);
        List<LockableChunk> lockableChunks = chunks.stream()
                .map(Chunk::writeLockable)
                .sorted()
                .peek(manager::setChunk)
                .peek(Lock::lock)
                .collect(Collectors.toList());

        try {
            for (LockableChunk c : lockableChunks)  {
                if (!c.isGenerated()) {
                    throw new IllegalStateException("Chunk should have been generated before");
                }
            }

            generator.populateChunk(manager, random, chunk.getX(), chunk.getZ());

            chunk.setPopulated();
            chunk.recalculateHeightMap();
            chunk.populateSkyLight();
            //chunk.setLightPopulated();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format("Generation error (%d, %d)", chunk.getX(), chunk.getZ()), e);
        } finally {
            manager.clean();
            lockableChunks.forEach(Lock::unlock);
        }

        return chunk;
    }
}
