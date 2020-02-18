package cn.nukkit.level.provider.anvil;

import cn.nukkit.Nukkit;
import cn.nukkit.level.LevelData;
import cn.nukkit.level.gamerule.GameRule;
import cn.nukkit.level.gamerule.GameRuleMap;
import cn.nukkit.level.provider.LevelDataSerializer;
import cn.nukkit.math.Vector3i;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.registry.GameRuleRegistry;
import cn.nukkit.utils.Identifier;
import cn.nukkit.utils.LoadState;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;

public class AnvilDataSerializer implements LevelDataSerializer {
    public static final LevelDataSerializer INSTANCE = new AnvilDataSerializer();

    private static final TypeReference<Map<String, Object>> OPTIONS_TYPE = new TypeReference<Map<String, Object>>() {};
    private static final int VERSION = 8;

    @Override
    public LoadState load(LevelData data, Path levelPath, String levelId) throws IOException {
        Path levelDatPath = levelPath.resolve("level.data");
        Path levelDatOldPath = levelPath.resolve("level.dat_old");

        if (Files.notExists(levelDatPath) && Files.notExists(levelDatOldPath)) {
            return LoadState.NOT_FOUND;
        }

        try {
            loadData(data, levelPath.resolve("level.data"));
        } catch (IOException e) {
            // Attempt to load backup
            loadData(data, levelPath.resolve("level.dat_old"));
        }
        return LoadState.LOADED;
    }

    @Override
    public void save(LevelData data, Path levelPath, String levelId) throws IOException {
        Path levelDatPath = levelPath.resolve("level.dat");
        Path levelDatOldPath = levelPath.resolve("level.dat_old");

        if (Files.exists(levelDatPath)) {
            Files.copy(levelDatPath, levelDatOldPath, StandardCopyOption.REPLACE_EXISTING);
        }

        saveData(data, levelDatPath);
    }

    private void saveData(LevelData data, Path levelDatPath) throws IOException {
        CompoundTag tag = new CompoundTag()
                .putString("LevelName", data.getName())
                .putString("generatorOptions", data.getGeneratorOptions())
                .putString("generatorName", data.getGenerator().toString())
                .putInt("thunderTime", data.getLightningTime())
                .putInt("Difficulty", data.getDifficulty())
                .putInt("GameType", data.getGameType())
                .putInt("serverChunkTickRange", data.getServerChunkTickRange())
                .putInt("NetherScale", data.getNetherScale())
                .putLong("currentTick", data.getCurrentTick())
                .putLong("LastPlayed", data.getLastPlayed())
                .putLong("RandomSeed", data.getRandomSeed())
                .putLong("Time", data.getTime())
                .putInt("SpawnX", data.getSpawn().getX())
                .putInt("SpawnY", data.getSpawn().getY())
                .putInt("SpawnZ", data.getSpawn().getZ())
                .putInt("Dimension", data.getDimension())
                .putInt("rainTime", data.getRainTime())
                .putFloat("rainLevel", data.getRainLevel())
                .putFloat("thunderLevel", data.getLightningLevel())
                .putBoolean("hardcore", data.isHardcore());

        // Gamerules - No idea why these aren't in a separate tag
        GameRuleMap gameRules = data.getGameRules();
        gameRules.forEach((gameRule, o) -> {
            String name = gameRule.getName().toLowerCase();
            if (gameRule.getValueClass() == Boolean.class) {
                tag.putBoolean(name, (boolean) o);
            } else if (gameRule.getValueClass() == Integer.class) {
                tag.putInt(name, (int) o);
            } else if (gameRule.getValueClass() == Boolean.class) {
                tag.putFloat(name, (float) o);
            }
        });

        // Write
        try (OutputStream stream = Files.newOutputStream(levelDatPath)) {
            NBTIO.writeGZIPCompressed(new CompoundTag("").putCompound("Data", tag), stream);
        }
    }

    private void loadData(LevelData data, Path levelDatPath) throws IOException {
        CompoundTag tag;
        try (InputStream stream = Files.newInputStream(levelDatPath)) {
            tag = NBTIO.readCompressed(stream);
        }

        tag.listenString("LevelName", data::setName);
        tag.listenString("generatorName", s -> data.setGenerator(Identifier.fromString(s)));
        tag.listenString("generatorOptions", data::setGeneratorOptions);
        tag.listenInt("thunderTime", data::setLightningTime);
        tag.listenInt("Difficulty", data::setDifficulty);
        tag.listenInt("GameType", data::setGameType);
        tag.listenInt("serverChunkTickRange", data::setServerChunkTickRange);
        tag.listenInt("NetherScale", data::setNetherScale);
        tag.listenLong("currentTick", data::setCurrentTick);
        tag.listenLong("LastPlayed", data::setLastPlayed);
        tag.listenLong("RandomSeed", data::setRandomSeed);
        tag.listenLong("Time", data::setTime);
        if (tag.contains("SpawnX") && tag.contains("SpawnY") && tag.contains("SpawnZ")) {
            int x = tag.getInt("SpawnX");
            int y = tag.getInt("SpawnY");
            int z = tag.getInt("SpawnZ");
            data.setSpawn(new Vector3i(x, y, z));
        }
        tag.listenInt("Dimension", data::setDimension);
        tag.listenInt("rainTime", data::setRainTime);
        tag.listenFloat("rainLevel", data::setRainLevel);
        tag.listenFloat("thunderLevel", data::setLightningLevel);
        tag.listenBoolean("hardcore", data::setHardcore);

        Map<String, Object> tagMap = tag.parseValue();
        GameRuleRegistry.get().getRules().forEach(rule -> {
            Object value = tagMap.get(rule.getName().toLowerCase());
            if (value instanceof Byte) {
                data.getGameRules().put((GameRule<Boolean>) rule, (byte) value != 0);
            } else if (value instanceof Integer) {
                data.getGameRules().put((GameRule<Integer>) rule, (int) value);
            } else if (value instanceof Float) {
                data.getGameRules().put((GameRule<Float>) rule, (float) value);
            }
        });
    }
}
