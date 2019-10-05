package cn.nukkit.level;

import cn.nukkit.Server;
import cn.nukkit.level.provider.LevelProvider;
import cn.nukkit.level.storage.StorageType;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nonnull;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

@Log4j2
public class LevelBuilder {
    private final Server server;
    private final LevelData levelData;
    private String id;
    private StorageType storageType;
    //private PlayerDataProvider playerDataProvider;

    public LevelBuilder(Server server) {
        this.server = server;
        this.levelData = new LevelData(server.getDefaultLevelData());
        this.storageType = server.getDefaultStorageType();
    }

    public LevelBuilder id(String id) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(id), "id is null or empty");
        this.id = id;
        return this;
    }

    public LevelBuilder seed(long seed) {
        this.levelData.setSeed(seed);
        return this;
    }

    public LevelBuilder storageType(StorageType storageType) {
        Preconditions.checkNotNull(storageType, "storageType");
        this.storageType = storageType;
        return this;
    }

    @Nonnull
    public CompletableFuture<Level> load() {
        Level loadedLevel = this.server.getLevel(id);
        if (loadedLevel != null) {
            return CompletableFuture.completedFuture(loadedLevel);
        }

        // Load chunk provider
        CompletableFuture<LevelProvider> providerFuture = CompletableFuture.supplyAsync(() -> {
            LevelProvider.Factory factory = this.server.getStorageRegistry().getLevelProviderFactory(storageType);
            if (factory == null) {
                throw new IllegalArgumentException("Unregistered storageType");
            }
            log.debug("Loading level provider: {}", id);
            try {
                LevelProvider provider = factory.create(id, Paths.get(server.getDataPath()).resolve("worlds"),
                        server.getScheduler().getAsyncPool());
                // Load level data
                provider.loadLevelData(levelData);
                return provider;
            } catch (Exception e) {
                throw new IllegalStateException("Unable to load level provider for level '" + id + "'", e);
            }
        });

        // Combine futures
        return providerFuture.thenApply(levelProvider -> {
            Level level = new Level(this.server, id, storageType, levelProvider, levelData);
            this.server.getLevelManager().register(level);
            level.init();
            level.setTickRate(this.server.getBaseTickRate());
            return level;
        });
    }
}
