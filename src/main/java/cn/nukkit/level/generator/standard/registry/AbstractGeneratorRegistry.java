package cn.nukkit.level.generator.standard.registry;

import cn.nukkit.Server;
import cn.nukkit.event.Event;
import cn.nukkit.level.generator.standard.StandardGeneratorUtils;
import cn.nukkit.registry.Registry;
import cn.nukkit.registry.RegistryException;
import cn.nukkit.utils.ConfigSection;
import cn.nukkit.utils.Identifier;
import com.google.common.base.Preconditions;
import lombok.NonNull;
import net.daporkchop.lib.random.PRandom;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.function.BiFunction;

/**
 * Base class for registries used by the NukkitX standard generator.
 *
 * @author DaPorkchop_
 */
public abstract class AbstractGeneratorRegistry<V> implements BiFunction<ConfigSection, PRandom, V>, Registry {
    private static final AtomicIntegerFieldUpdater<AbstractGeneratorRegistry> CLOSED_UPDATER
            = AtomicIntegerFieldUpdater.newUpdater(AbstractGeneratorRegistry.class, "closed");

    protected final Map<Identifier, BiFunction<ConfigSection, PRandom, V>> idToValues = new IdentityHashMap<>();

    private volatile int closed = 0;

    public AbstractGeneratorRegistry() {
        this.registerDefault();

        Event event = this.constructionEvent();
        Server.getInstance().getPluginManager().callEvent(event);
        this.close();
    }

    public void register(@NonNull Identifier id, @NonNull BiFunction<ConfigSection, PRandom, V> value) {
        Preconditions.checkState(this.idToValues.putIfAbsent(id, value) == null, "ID \"%s\" already registered!", id);
    }

    @Override
    public V apply(@NonNull ConfigSection config, @NonNull PRandom random) {
        Preconditions.checkState(this.closed == 1, "not closed");
        Identifier id = StandardGeneratorUtils.getId(config, "id");
        return Preconditions.checkNotNull(this.idToValues.get(id), "Unknown ID \"%s\"", id)
                .apply(config, random);
    }

    public boolean isRegistered(@NonNull Identifier id) {
        Preconditions.checkState(this.closed == 1, "not closed");
        return this.idToValues.containsKey(id);
    }

    @Override
    public void close() throws RegistryException {
        if (!CLOSED_UPDATER.compareAndSet(this, 0, 1)) {
            throw new RegistryException("already closed");
        }
    }

    protected abstract void registerDefault();

    protected abstract Event constructionEvent();
}
