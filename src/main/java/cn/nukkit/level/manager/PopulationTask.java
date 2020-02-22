package cn.nukkit.level.manager;

import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.level.chunk.LockableChunk;
import cn.nukkit.level.generator.Generator;
import com.google.common.base.Preconditions;
import net.daporkchop.lib.random.PRandom;
import net.daporkchop.lib.random.impl.FastJavaPRandom;

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
public final class PopulationTask implements BiFunction<Chunk, List<Chunk>, Chunk> {
    private final Generator generator;

    public PopulationTask(Generator generator) {
        this.generator = Preconditions.checkNotNull(generator, "generator");
    }

    @Override
    public Chunk apply(Chunk chunk, List<Chunk> chunks) {
        if (chunk == null || chunk.isPopulated())   {
            return chunk;
        } else if (!chunk.isGenerated())    {
            throw new IllegalStateException("Cannot populate chunk before it is generated! x=" + chunk.getX() + ",z=" + chunk.getZ());
        }

        PRandom random = new FastJavaPRandom(~(chunk.key() ^ chunk.getLevel().getSeed()));

        chunks.add(chunk);
        List<LockableChunk> lockableChunks = chunks.stream()
                .map(Chunk::writeLockable)
                .sorted()
                .peek(populationChunk -> Preconditions.checkState(populationChunk.isGenerated(), "Chunk %d,%d was used for population before being generated!", populationChunk.getX(), populationChunk.getZ()))
                .peek(Lock::lock)
                .collect(Collectors.toList());
        try {
            this.generator.populate(random, PopulationChunkManager.create(chunk, lockableChunks), chunk.getX(), chunk.getZ());
            chunk.setPopulated(true);
        } finally {
            lockableChunks.forEach(Lock::unlock);
        }
        return chunk;
    }
}
