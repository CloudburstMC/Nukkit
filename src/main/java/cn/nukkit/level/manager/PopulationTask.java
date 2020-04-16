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
import net.daporkchop.lib.random.impl.FastJavaPRandom;
import net.daporkchop.lib.random.impl.FastPRandom;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * Delegates chunk population to a {@link Generator}.
 *
 * @author DaPorkchop_
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PopulationTask implements BiFunction<Chunk, List<Chunk>, Chunk> {
    public static final PopulationTask INSTANCE = new PopulationTask();

    @Override
    public Chunk apply(@NonNull Chunk chunk, List<Chunk> chunks) {
        if (chunk.isPopulated())    {
            return chunk;
        }
        Preconditions.checkState(chunk.isGenerated(), "Chunk %s,%s was populated before being generated!", chunk.getX(), chunk.getZ());

        PRandom random = new FastPRandom(chunk.getX() * 6169336838570288771L ^ chunk.getZ() * 1173358236373774883L ^ chunk.getLevel().getSeed());

        chunks.add(chunk);
        LockableChunk[] lockableChunks = chunks.stream()
                .peek(populationChunk -> Preconditions.checkState(populationChunk.isGenerated(), "Chunk %d,%d was used for population before being generated!", populationChunk.getX(), populationChunk.getZ()))
                .map(Chunk::writeLockable)
                .sorted()
                .peek(Lock::lock)
                .toArray(LockableChunk[]::new);
        try {
            chunk.getLevel().getGenerator().populate(random, new PopulationChunkManager(chunk, lockableChunks, chunk.getLevel().getSeed()), chunk.getX(), chunk.getZ());
            chunk.setState(IChunk.STATE_POPULATED);
            chunk.setDirty();
        } finally {
            for (LockableChunk lockableChunk : lockableChunks)  {
                lockableChunk.unlock();
            }
        }
        return chunk;
    }
}
