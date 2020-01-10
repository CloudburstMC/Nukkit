package cn.nukkit.level.generator.function;

import cn.nukkit.level.Level;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.level.chunk.LockableChunk;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.level.generator.SimpleChunkManager;
import com.google.common.base.Preconditions;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.function.BiFunction;

@Log4j2
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

        List<LockableChunk> lockableChunks = new ArrayList<>();
        lockableChunks.add(chunk.lockable());

        for (Chunk aroundChunk : chunks) {
            Preconditions.checkNotNull(aroundChunk, "aroundChunk");
            if (!aroundChunk.isGenerated()) {
                throw new IllegalStateException("Chunk should have been loaded before");
            }
            lockableChunks.add(aroundChunk.lockable());
        }

        manager.setSeed(this.level.getSeed());
        lockableChunks.forEach(manager::setChunk);
        lockableChunks.forEach(Lock::lock);
        try {
            generator.populateChunk(chunk.getX(), chunk.getZ());

            chunk.setPopulated();
            chunk.recalculateHeightMap();
            chunk.populateSkyLight();
            //chunk.setLightPopulated();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format("Generation error (%d, %d)", chunk.getX(), chunk.getZ()), e);
        } finally {
            manager.clean();
            for (LockableChunk lockableChunk : lockableChunks) {
                lockableChunk.unlock();
                lockableChunk.setDirty(false);
            }
        }

        return chunk;
    }
}
