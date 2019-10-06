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

        long seed = tag.getAsLong("RandomSeed");
        long savedTime = tag.getAsLong("Time");
        long savedTick = tag.getAsLong("currentTick");
        int generatorInt = tag.getAsInt("ChunkGenerator");
        Generator generator = (generatorInt > 5 || generatorInt < 0 ? Generator.OVERWORLD : Generator.values()[generatorInt]);

        final LevelDBLevelDataProvider levelData = new LevelDBLevelDataProvider(levelDatPath, seed, savedTime, savedTick, generator);

        // Level Data
        tag.listenForString("LevelName", levelData::setName);

        // Level Settings
        tag.listenForInt("Difficulty", integer -> levelData.setDifficulty(Difficulty.parse(integer)));
        tag.listenForInt("GameMode", integer -> levelData.setGameMode(GameMode.parse(integer)));
        tag.listenForInt("SpawnX", x -> {
            tag.listenForInt("SpawnY", y -> {
                tag.listenForInt("SpawnZ", z -> {
                    levelData.setDefaultSpawn(Vector3i.from(x, y, z).toFloat());
                });
            });
        });

        // Game Rules
        for (GameRule gameRule : GameRule.values()) {
            String key = gameRule.getName().toLowerCase();
            switch (gameRule.getType()) {
                case BOOLEAN:
                    tag.listenForByte(key, value -> levelData.getGameRules().setGameRule(gameRule, value != 0));
                    break;
                case INTEGER:
                    tag.listenForInt(key, value -> levelData.getGameRules().setGameRule(gameRule, value));
                    break;
                case FLOAT:
                    tag.listenForFloat(key, value -> levelData.getGameRules().setGameRule(gameRule, value));
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
