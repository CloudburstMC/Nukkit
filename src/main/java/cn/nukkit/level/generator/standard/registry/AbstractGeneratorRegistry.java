package cn.nukkit.level.generator.standard.registry;

import cn.nukkit.Server;
import cn.nukkit.event.Event;
import cn.nukkit.registry.Registry;
import cn.nukkit.registry.RegistryException;
import cn.nukkit.utils.Identifier;
import com.google.common.base.Preconditions;
import lombok.NonNull;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * Base class for registries used by the NukkitX standard generator.
 *
 * @author DaPorkchop_
 */
public abstract class AbstractGeneratorRegistry<V> implements Registry {
    private static final AtomicIntegerFieldUpdater<AbstractGeneratorRegistry> CLOSED_UPDATER
            = AtomicIntegerFieldUpdater.newUpdater(AbstractGeneratorRegistry.class, "closed");

    protected final Map<Identifier, Class<? extends V>> idToValues = new IdentityHashMap<>();

    private volatile int closed = 0;

    public AbstractGeneratorRegistry() {
        this.registerDefault();

        Event event = this.constructionEvent();
        if (Server.getInstance() != null) {
            //i was debugging stuff
            Server.getInstance().getPluginManager().callEvent(event);
        }
        this.close();
    }

    public void register(@NonNull Identifier id, @NonNull Class<? extends V> clazz) {
        Preconditions.checkState(this.idToValues.putIfAbsent(id, clazz) == null, "ID \"%s\" already registered!", id);
    }

    public Class<? extends V> get(@NonNull Identifier id) {
        Preconditions.checkState(this.closed == 1, "not closed");
        return Preconditions.checkNotNull(this.idToValues.get(id), id.toString());
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
