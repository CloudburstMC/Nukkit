package cn.nukkit.level.manager;

import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.level.chunk.LockableChunk;
import cn.nukkit.level.generator.Generator;
import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.daporkchop.lib.random.PRandom;
import net.daporkchop.lib.random.impl.FastPRandom;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.function.BiFunction;

/**
 * Delegates chunk population to a {@link Generator}.
 *
 * @author DaPorkchop_
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PopulationAroundTask implements BiFunction<Chunk, List<Chunk>, Chunk> {
    public static final PopulationAroundTask INSTANCE = new PopulationAroundTask();

    @Override
    public Chunk apply(Chunk chunk, List<Chunk> chunks) {
        if (Preconditions.checkNotNull(chunk, "chunk").isPopulatedAround())    {
            return chunk;
        }
        Preconditions.checkState(chunk.isPopulated(), "Chunk %s,%s was populated around before being populated!", chunk.getX(), chunk.getZ());

        chunk.setPopulatedAround(true);
        return chunk;
    }
}
