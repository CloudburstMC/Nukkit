package cn.nukkit.level;

import cn.nukkit.Server;
import cn.nukkit.level.provider.LevelProvider;
import cn.nukkit.level.provider.LevelProviderFactory;
import cn.nukkit.registry.StorageRegistry;
import cn.nukkit.utils.Identifier;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Log4j2
@NotThreadSafe
public class LevelBuilder {
    private final Server server;
    private final LevelData levelData;
    private String id;
    private Identifier storageId;
    //private PlayerDataProvider playerDataProvider;

    public LevelBuilder(Server server) {
        this.server = server;
        this.levelData = new LevelData(server.getDefaultLevelData());
    }

    public LevelBuilder id(String id) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(id), "id is null or empty");
        this.levelData.setName(this.id = id);
        return this;
    }

    public LevelBuilder seed(long seed) {
        this.levelData.setRandomSeed(seed);
        return this;
    }

    public LevelBuilder storage(Identifier storageId) {
        Preconditions.checkNotNull(storageId, "storageType");
        this.storageId = storageId;
        return this;
    }

    public LevelBuilder generator(Identifier generatorId) {
        Preconditions.checkNotNull(generatorId, "generatorId");
        Preconditions.checkArgument(this.server.getGeneratorRegistry().isRegistered(generatorId), "Unknown generator: \"%s\"", generatorId);
        this.levelData.setGenerator(generatorId);
        return this;
    }

    public LevelBuilder generatorOptions(String generatorOptions) {
        Preconditions.checkNotNull(generatorOptions, "generatorOptions");
        this.levelData.setGeneratorOptions(generatorOptions);
        return this;
    }

    @Nonnull
    public CompletableFuture<Level> load() {
        Level loadedLevel = this.server.getLevel(id);
        if (loadedLevel != null) {
            return CompletableFuture.completedFuture(loadedLevel);
        }
        final Path worldsPath = Paths.get(server.getDataPath()).resolve("worlds");

        // If storageId isn't set detect or set default.
        if (storageId == null) {
            storageId = StorageRegistry.get().detectStorage(id, worldsPath);
            if (storageId == null) {
                storageId = server.getDefaultStorageId();
            }
        }

        final Executor executor = this.server.getLevelManager().getChunkExecutor();

        // Load chunk provider
        CompletableFuture<LevelProvider> providerFuture = CompletableFuture.supplyAsync(() -> {
            LevelProviderFactory factory = this.server.getStorageRegistry().getLevelProviderFactory(storageId);
            if (factory == null) {
                throw new IllegalArgumentException("Unregistered storageType");
            }
            try {
                LevelProvider provider = factory.create(id, worldsPath, executor);
                // Load level data
                provider.loadLevelData(levelData);
                return provider;
            } catch (Exception e) {
                throw new IllegalStateException("Unable to load level provider for level '" + id + "'", e);
            }
        }, executor);

        // Combine futures
        return providerFuture.thenApply(levelProvider -> {
            Level level = new Level(this.server, id, levelProvider, levelData);
            this.server.getLevelManager().register(level);
            level.init();
            level.setTickRate(this.server.getBaseTickRate());
            return level;
        });
    }
}
