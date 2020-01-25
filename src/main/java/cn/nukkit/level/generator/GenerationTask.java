package cn.nukkit.level.generator;

import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.level.chunk.LockableChunk;
import com.google.common.base.Preconditions;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

/**
 * Delegates chunk generation to a {@link Generator}.
 *
 * @author DaPorkchop_
 */
public final class GenerationTask implements Function<Chunk, Chunk> {
    private final Generator generator;

    public GenerationTask(Generator generator) {
        this.generator = Preconditions.checkNotNull(generator, "generator");
    }

    @Override
    public Chunk apply(Chunk chunk) {
        if (chunk.isGenerated()) {
            return chunk;
        }

        boolean retainDirty = false;
        LockableChunk lockable = chunk.lockable();

        lockable.lock();
        try {
            retainDirty = this.generator.generate(ThreadLocalRandom.current(), lockable, chunk.getX(), chunk.getZ());
            chunk.setGenerated();
        } finally {
            if (!retainDirty)   {
                chunk.setDirty(false);
            }
            lockable.unlock();
        }
        return chunk;
    }
}
