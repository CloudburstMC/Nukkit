package com.nukkitx.server.level.provider.leveldb;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Preconditions;
import com.nukkitx.api.level.data.Difficulty;
import com.nukkitx.api.level.data.GameRule;
import com.nukkitx.api.level.data.Generator;
import com.nukkitx.api.util.GameMode;
import com.nukkitx.nbt.NbtUtils;
import com.nukkitx.nbt.stream.NBTInputStream;
import com.nukkitx.nbt.tag.CompoundTag;
import com.nukkitx.nbt.tag.IntTag;
import com.nukkitx.nbt.tag.LongTag;
import com.nukkitx.server.level.NukkitLevelData;
import com.nukkitx.server.level.provider.LevelDataProvider;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class LevelDBLevelDataProvider extends NukkitLevelData implements LevelDataProvider {
    private final Path levelDatPath;

    private LevelDBLevelDataProvider(Path levelDatPath) {
        super();
        this.levelDatPath = levelDatPath;
    }

    private LevelDBLevelDataProvider(Path levelDatPath, long seed, long savedTime, long savedTick, Generator generator) {
        super(seed, savedTick, savedTime, generator);
        this.levelDatPath = levelDatPath;
    }

    public static LevelDBLevelDataProvider load(@Nonnull Path levelDatPath) throws IOException {
        Preconditions.checkNotNull(levelDatPath, "levelDatPath");
        if (Files.notExists(levelDatPath) || !Files.isRegularFile(levelDatPath)) {
            Files.deleteIfExists(levelDatPath);
            return new LevelDBLevelDataProvider(levelDatPath);
        }
        CompoundTag tag;
        try (InputStream stream = Files.newInputStream(levelDatPath)) {
            if (stream.available() < 9) {
                return new LevelDBLevelDataProvider(levelDatPath);
            }
            stream.skip(8);
            NBTInputStream reader = NbtUtils.createReaderLE(stream);
            tag = (CompoundTag) reader.readTag();
        }

        if (tag == null) {
            return new LevelDBLevelDataProvider(levelDatPath);
        }

        long seed = tag.getAs("RandomSeed", LongTag.class).getPrimitiveValue();
        long savedTime = tag.getAs("Time", LongTag.class).getPrimitiveValue();
        long savedTick = tag.getAs("currentTick", LongTag.class).getPrimitiveValue();
        int generatorInt = tag.getAs("ChunkGenerator", IntTag.class).getPrimitiveValue();
        Generator generator = (generatorInt > 5 || generatorInt < 0 ? Generator.OVERWORLD : Generator.values()[generatorInt]);

        final LevelDBLevelDataProvider levelData = new LevelDBLevelDataProvider(levelDatPath, seed, savedTime, savedTick, generator);

        // Level Data
        tag.listen("LevelName", String.class, levelData::setName);

        // Level Settings
        tag.listen("Difficulty", Integer.class, integer -> levelData.setDifficulty(Difficulty.parse(integer)));
        tag.listen("GameMode", Integer.class, integer -> levelData.setGameMode(GameMode.parse(integer)));
        tag.listen("SpawnX", Integer.class, x -> {
            tag.listen("SpawnY", Integer.class, y -> {
                tag.listen("SpawnZ", Integer.class, z -> {
                    levelData.setDefaultSpawn(Vector3i.from(x, y, z).toFloat());
                });
            });
        });

        // Game Rules
        for (GameRule gameRule : GameRule.values()) {
            String key = gameRule.getName().toLowerCase();
            switch (gameRule.getType()) {
                case BOOLEAN:
                    tag.listen(key, Byte.class, value -> levelData.getGameRules().setGameRule(gameRule, value != 0));
                    break;
                case INTEGER:
                    tag.listen(key, Integer.class, value -> levelData.getGameRules().setGameRule(gameRule, value));
                    break;
                case FLOAT:
                    tag.listen(key, Float.class, value -> levelData.getGameRules().setGameRule(gameRule, value));
            }
        }

        return levelData;
    }

    @Override
    public void save() {
        save(levelDatPath);
    }

    @Override
    public void save(Path path) {

    }

    @Override
    public Path getPath() {
        return levelDatPath;
    }
}
