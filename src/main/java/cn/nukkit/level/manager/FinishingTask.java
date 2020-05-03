package cn.nukkit.level.manager;

import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.level.chunk.LockableChunk;
import cn.nukkit.level.generator.Generator;
import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.daporkchop.lib.random.PRandom;
import net.daporkchop.lib.random.impl.FastPRandom;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.function.BiFunction;

/**
 * Delegates chunk finishing to a {@link Generator}.
 *
 * @author DaPorkchop_
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FinishingTask implements BiFunction<Chunk, List<Chunk>, Chunk> {
    public static final FinishingTask INSTANCE = new FinishingTask();

    @Override
    public Chunk apply(@NonNull Chunk chunk, List<Chunk> chunks) {
        if (chunk.isFinished())    {
            return chunk;
        }
        Preconditions.checkState(chunk.isPopulated(), "Chunk %s,%s was finished before being populated!", chunk.getX(), chunk.getZ());

        PRandom random = new FastPRandom(chunk.getX() * 9050650275199519859L ^ chunk.getZ() * 5251710924988638743L ^ chunk.getLevel().getSeed());

        chunks.add(chunk);
        LockableChunk[] lockableChunks = chunks.stream()
                .peek(populationChunk -> Preconditions.checkState(populationChunk.isGenerated(), "Chunk %d,%d was used for population before being generated!", populationChunk.getX(), populationChunk.getZ()))
                .map(Chunk::writeLockable)
                .sorted()
                .peek(Lock::lock)
                .toArray(LockableChunk[]::new);
        try {
            chunk.getLevel().getGenerator().finish(random, new PopulationChunkManager(chunk, lockableChunks, chunk.getLevel().getSeed()), chunk.getX(), chunk.getZ());
            chunk.setState(IChunk.STATE_FINISHED);
            chunk.setDirty();
        } finally {
            for (LockableChunk lockableChunk : lockableChunks)  {
                lockableChunk.unlock();
            }
        }
        return chunk;
    }
}
