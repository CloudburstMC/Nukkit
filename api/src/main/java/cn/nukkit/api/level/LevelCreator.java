package cn.nukkit.api.level;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.nio.file.Path;

@Value
@Builder
public class LevelCreator {
    @NonNull
    private final String name;
    @NonNull
    private final String id;
    @NonNull
    private final LevelStorage storage;
    private final boolean readOnly;
    @NonNull
    private final Path levelPath;
    private final boolean loadSpawnChunks;
    @NonNull
    private final String generatorSettings;
    @NonNull
    private final String generator;

    public enum LevelStorage {
        ANVIL,
        LEVELDB,
        NULL
    }
}
