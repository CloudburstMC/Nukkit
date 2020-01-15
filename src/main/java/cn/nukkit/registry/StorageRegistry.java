package cn.nukkit.registry;

import cn.nukkit.level.provider.LevelProviderFactory;
import cn.nukkit.level.provider.anvil.AnvilProviderFactory;
import cn.nukkit.level.provider.leveldb.LevelDBProviderFactory;
import cn.nukkit.level.storage.StorageIds;
import cn.nukkit.utils.Identifier;
import com.google.common.base.Preconditions;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;

public class StorageRegistry implements Registry {
    private static final StorageRegistry INSTANCE = new StorageRegistry();
    private final Map<Identifier, LevelProviderFactory> providers = new IdentityHashMap<>();
    private volatile boolean closed;

    private StorageRegistry() {
        this.registerVanillaStorage();
    }

    public static StorageRegistry get() {
        return INSTANCE;
    }

    public synchronized void register(Identifier identifier, LevelProviderFactory levelProviderFactory)
            throws RegistryException {
        Objects.requireNonNull(identifier, "type");
        Objects.requireNonNull(levelProviderFactory, "levelProviderFactory");

        Preconditions.checkArgument(!this.providers.containsKey(identifier));
        this.providers.put(identifier, levelProviderFactory);
    }

    public LevelProviderFactory getLevelProviderFactory(Identifier identifier) {
        Objects.requireNonNull(identifier, "identifier");

        return this.providers.get(identifier);
    }

    public boolean isRegistered(Identifier identifier) {
        return this.providers.containsKey(identifier);
    }

    @Override
    public void close() {
        Preconditions.checkArgument(!this.closed, "Registry has already been closed");
        this.closed = true;
    }

    private void registerVanillaStorage() throws RegistryException {
        this.register(StorageIds.ANVIL, AnvilProviderFactory.INSTANCE);
        this.register(StorageIds.LEVELDB, LevelDBProviderFactory.INSTANCE);
    }
}
