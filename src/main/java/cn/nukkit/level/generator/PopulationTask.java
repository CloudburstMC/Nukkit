package cn.nukkit.level.generator;

import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.level.chunk.LockableChunk;
import cn.nukkit.level.manager.PopulationChunkManager;
import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiFunction;

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
    public Chunk apply(Chunk chunk, List<Chunk> populationChunks) {
        if (chunk == null || chunk.isPopulated())   {
            return chunk;
        } else if (!chunk.isGenerated())    {
            throw new IllegalStateException("Cannot populate chunk before it is generated! x=" + chunk.getX() + ",z=" + chunk.getZ());
        }

        boolean retainDirty = false;
        List<LockableChunk> lockable = new ArrayList<>(populationChunks.size() + 1);
        lockable.add(chunk.writeLockable());
        for (Chunk populationChunk : populationChunks)    {
            if (!Preconditions.checkNotNull(populationChunk, "populationChunk").isGenerated()) {
                throw new IllegalStateException("Population chunk for x=" + chunk.getX() + ",z=" + chunk.getZ()
                        + " is not generated! x=" + populationChunk.getX() + ",z=" + populationChunk.getZ());
            }
            lockable.add(populationChunk.writeLockable());
        }

        lockable.forEach(LockableChunk::lock);
        try {
            retainDirty = this.generator.populate(ThreadLocalRandom.current(), new PopulationChunkManager(chunk, populationChunks), chunk.getX(), chunk.getZ());
            chunk.setPopulated(true);
        } finally {
            if (!retainDirty)   {
                lockable.forEach(LockableChunk::setNotDirty);
            }
            lockable.forEach(LockableChunk::unlock);
        }
        return chunk;
    }
}
