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

import java.util.function.Function;

/**
 * Delegates chunk generation to a {@link Generator}.
 *
 * @author DaPorkchop_
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GenerationTask implements Function<Chunk, Chunk> {
    public static final GenerationTask INSTANCE = new GenerationTask();

    @Override
    public Chunk apply(@NonNull Chunk chunk) {
        if (chunk.isGenerated()) {
            return chunk;
        }

        PRandom random = new FastPRandom(chunk.getX() * 3053330778986901431L ^ chunk.getZ() * 1517227374085824433L ^ chunk.getLevel().getSeed());
        LockableChunk lockable = chunk.writeLockable();

        lockable.lock();
        try {
            chunk.getLevel().getGenerator().generate(random, lockable, chunk.getX(), chunk.getZ());
            chunk.setState(IChunk.STATE_GENERATED);
            chunk.setDirty();
        } finally {
            lockable.unlock();
        }
        return chunk;
    }
}
