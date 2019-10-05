package cn.nukkit.registry;

import cn.nukkit.level.provider.LevelProvider;
import cn.nukkit.level.provider.anvil.AnvilProvider;
import cn.nukkit.level.provider.leveldb.LevelDBProvider;
import cn.nukkit.level.storage.StorageType;
import cn.nukkit.level.storage.StorageTypes;
import cn.nukkit.utils.Identifier;
import com.google.common.base.Preconditions;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class StorageRegistry implements Registry {
    private final Map<Identifier, StorageType> identifiers = new IdentityHashMap<>();
    private final Map<StorageType, LevelProvider.Factory> providers = new IdentityHashMap<>();
    private volatile boolean closed;

    public StorageRegistry() {
        this.registerVanillaTypes();
    }

    public synchronized void register(StorageType type, LevelProvider.Factory levelProviderFactory)
            throws RegistryException {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(levelProviderFactory, "levelProviderFactory");

        Identifier identifier = type.getIdentifier();
        Preconditions.checkArgument(!this.identifiers.containsKey(identifier));

        this.identifiers.put(identifier, type);
        this.providers.put(type, levelProviderFactory);
    }

    public Optional<StorageType> fromIdentifier(String identifier) {
        return this.fromIdentifier(Identifier.fromString(identifier));
    }

    public Optional<StorageType> fromIdentifier(Identifier identifier) {
        return Optional.ofNullable(this.identifiers.get(identifier));
    }

    public LevelProvider.Factory getLevelProviderFactory(StorageType type) {
        Objects.requireNonNull(type, "type");

        return this.providers.get(type);
    }

    @Override
    public void close() {
        Preconditions.checkArgument(!this.closed, "Registry has already been closed");
        this.closed = true;
    }

    private void registerVanillaTypes() {
        this.register(StorageTypes.ANVIL, AnvilProvider.FACTORY);
        this.register(StorageTypes.LEVELDB, LevelDBProvider.FACTORY);
    }
}
