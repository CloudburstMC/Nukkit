package cn.nukkit.level.generator.standard.store;

import cn.nukkit.utils.Identifier;
import com.google.common.base.Preconditions;
import lombok.NonNull;
import net.daporkchop.lib.common.function.io.IOFunction;

import java.io.IOException;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Base class for stores used by the NukkitX standard generator.
 *
 * @author DaPorkchop_
 */
public abstract class AbstractGeneratorStore<V> {
    protected final Set<Identifier> recursionLock = new HashSet<>();

    protected final Map<Identifier, V> idToValues = new IdentityHashMap<>();

    //don't make tons of lambda objects
    protected final IOFunction<Identifier, V> computeFunction = this::compute;

    public synchronized V find(@NonNull Identifier id) {
        Preconditions.checkState(this.recursionLock.add(id), "Recursively loading %s...", id);
        try {
            return this.idToValues.computeIfAbsent(id, this.computeFunction);
        } finally {
            this.recursionLock.remove(id);
        }
    }

    protected abstract V compute(@NonNull Identifier id) throws IOException;
}
