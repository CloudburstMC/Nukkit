/*
 * GNU GENERAL PUBLIC LICENSE
 * Copyright (C) 2018 NukkitX Project
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * verion 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * Contact info: info@nukkitx.com
 */

package cn.nukkit.server.level.provider.leveldb;

import cn.nukkit.api.level.data.Difficulty;
import cn.nukkit.api.level.data.GameRule;
import cn.nukkit.api.level.data.Generator;
import cn.nukkit.api.util.GameMode;
import cn.nukkit.server.level.NukkitLevelData;
import cn.nukkit.server.level.provider.LevelDataProvider;
import cn.nukkit.server.nbt.NBTIO;
import cn.nukkit.server.nbt.stream.NBTInputStream;
import cn.nukkit.server.nbt.tag.CompoundTag;
import cn.nukkit.server.nbt.tag.IntTag;
import cn.nukkit.server.nbt.tag.LongTag;
import cn.nukkit.server.nbt.tag.StringTag;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

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
            NBTInputStream reader = NBTIO.createReaderLE(stream);
            tag = (CompoundTag) reader.readTag();
        }

        if (tag == null) {
            return new LevelDBLevelDataProvider(levelDatPath);
        }

        long seed = tag.getAsLong("RandomSeed").map(LongTag::getPrimitiveValue).orElse(-1L);
        long savedTime = tag.getAsLong("Time").map(LongTag::getPrimitiveValue).orElse(0L);
        long savedTick = tag.getAsLong("currentTick").map(LongTag::getPrimitiveValue).orElse(0L);
        int generatorInt = tag.getAsInt("ChunkGenerator").map(IntTag::getPrimitiveValue).orElse(0);
        Generator generator = (generatorInt > 5 || generatorInt < 0 ? Generator.OVERWORLD : Generator.values()[generatorInt]);

        final LevelDBLevelDataProvider levelData = new LevelDBLevelDataProvider(levelDatPath, seed, savedTime, savedTick, generator);

        // Level Data
        levelData.setName(tag.getAsString("LevelName").map(StringTag::getValue).orElse("New World"));

        // Level Settings
        levelData.setDifficulty(Difficulty.parse(tag.getAsInt("Difficulty").map(IntTag::getPrimitiveValue).orElse(1)));
        levelData.setGameMode(GameMode.parse(tag.getAsInt("GameMode").map(IntTag::getPrimitiveValue).orElse(0)));
        Optional<IntTag> spawnX = tag.getAsInt("SpawnX");
        Optional<IntTag> spawnY = tag.getAsInt("SpawnY");
        Optional<IntTag> spawnZ = tag.getAsInt("SpawnZ");
        if (spawnX.isPresent() && spawnY.isPresent() && spawnZ.isPresent()) {
            levelData.setDefaultSpawn(new Vector3i(
                    spawnX.get().getPrimitiveValue(),
                    spawnY.get().getPrimitiveValue(),
                    spawnZ.get().getPrimitiveValue()
            ).toFloat());
        }

        // Game Rules
        for (GameRule gameRule : GameRule.values()) {
            String key = gameRule.getName().toLowerCase();
            switch (gameRule.getType()) {
                case BOOLEAN:
                    tag.getAsByte(key).ifPresent(byteTag -> levelData.getGameRules().setGameRule(gameRule, byteTag.getAsBoolean()));
                    break;
                case INTEGER:
                    tag.getAsInt(key).ifPresent(intTag -> levelData.getGameRules().setGameRule(gameRule, intTag.getPrimitiveValue()));
                    break;
                case FLOAT:
                    tag.getAsFloat(key).ifPresent(floatTag -> levelData.getGameRules().setGameRule(gameRule, floatTag.getPrimitiveValue()));
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
