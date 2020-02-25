package cn.nukkit.level.generator.standard.store;

import cn.nukkit.utils.Identifier;
import lombok.NonNull;
import net.daporkchop.lib.common.function.io.IOFunction;

import java.io.IOException;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Base class for stores used by the NukkitX standard generator.
 *
 * @author DaPorkchop_
 */
public abstract class AbstractGeneratorStore<V> implements Function<Identifier, V> {
    protected final Map<Identifier, V> idToValues = new IdentityHashMap<>();

    //don't make tons of lambda objects
    protected final IOFunction<Identifier, V> computeFunction = this::compute;

    public synchronized V find(@NonNull Identifier id) {
        return this.idToValues.computeIfAbsent(id, this.computeFunction);
    }

    protected abstract V compute(@NonNull Identifier id) throws IOException;

    @Override
    public V apply(@NonNull Identifier id) {
        return this.find(id);
    }
}
