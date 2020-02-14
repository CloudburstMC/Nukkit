package cn.nukkit.level.provider.anvil;

import cn.nukkit.Nukkit;
import cn.nukkit.level.LevelData;
import cn.nukkit.level.gamerule.GameRule;
import cn.nukkit.level.gamerule.GameRuleMap;
import cn.nukkit.level.provider.LevelDataSerializer;
import cn.nukkit.registry.GameRuleRegistry;
import cn.nukkit.utils.Identifier;
import cn.nukkit.utils.LoadState;
import com.fasterxml.jackson.core.type.TypeReference;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.nbt.CompoundTagBuilder;
import com.nukkitx.nbt.NbtUtils;
import com.nukkitx.nbt.stream.NBTInputStream;
import com.nukkitx.nbt.stream.NBTOutputStream;
import com.nukkitx.nbt.tag.CompoundTag;

import java.io.IOException;
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
        CompoundTagBuilder tag = CompoundTag.builder()
                .stringTag("LevelName", data.getName())
                .stringTag("generatorOptions", Nukkit.JSON_MAPPER.writeValueAsString(data.getGeneratorOptions()))
                .stringTag("generatorName", data.getGenerator().toString())
                .intTag("thunderTime", data.getLightningTime())
                .intTag("Difficulty", data.getDifficulty())
                .intTag("GameType", data.getGameType())
                .intTag("serverChunkTickRange", data.getServerChunkTickRange())
                .intTag("NetherScale", data.getNetherScale())
                .longTag("currentTick", data.getCurrentTick())
                .longTag("LastPlayed", data.getLastPlayed())
                .longTag("RandomSeed", data.getRandomSeed())
                .longTag("Time", data.getTime())
                .intTag("SpawnX", data.getSpawn().getX())
                .intTag("SpawnY", data.getSpawn().getY())
                .intTag("SpawnZ", data.getSpawn().getZ())
                .intTag("Dimension", data.getDimension())
                .intTag("rainTime", data.getRainTime())
                .floatTag("rainLevel", data.getRainLevel())
                .floatTag("thunderLevel", data.getLightningLevel())
                .booleanTag("hardcore", data.isHardcore());

        CompoundTagBuilder gameRulesTag = CompoundTag.builder();
        GameRuleMap gameRules = data.getGameRules();
        gameRules.forEach((gameRule, o) -> {
            String name = gameRule.getName();
            gameRulesTag.stringTag(name, o.toString());
        });
        tag.tag(gameRulesTag.build("GameRules"));

        // Write
        try (NBTOutputStream stream = NbtUtils.createWriter(Files.newOutputStream(levelDatPath))) {
            stream.write(CompoundTag.builder()
                    .tag(tag.build("Data"))
                    .buildRootTag());
        }
    }

    private void loadData(LevelData data, Path levelDatPath) throws IOException {
        CompoundTag tag;
        try (NBTInputStream stream = NbtUtils.createReader(Files.newInputStream(levelDatPath))) {
            tag = (CompoundTag) stream.readTag();
        }

        tag.listenForString("LevelName", data::setName);
        if (tag.contains("generatorOptions")) {
            data.setGeneratorOptions(tag.getString("generatorOptions"));
        }
        tag.listenForString("generatorName", s -> data.setGenerator(Identifier.fromString(s)));
        tag.listenForInt("thunderTime", data::setLightningTime);
        tag.listenForInt("Difficulty", data::setDifficulty);
        tag.listenForInt("GameType", data::setGameType);
        tag.listenForInt("serverChunkTickRange", data::setServerChunkTickRange);
        tag.listenForInt("NetherScale", data::setNetherScale);
        tag.listenForLong("currentTick", data::setCurrentTick);
        tag.listenForLong("LastPlayed", data::setLastPlayed);
        tag.listenForLong("RandomSeed", data::setRandomSeed);
        tag.listenForLong("Time", data::setTime);
        if (tag.contains("SpawnX") && tag.contains("SpawnY") && tag.contains("SpawnZ")) {
            int x = tag.getInt("SpawnX");
            int y = tag.getInt("SpawnY");
            int z = tag.getInt("SpawnZ");
            data.setSpawn(Vector3i.from(x, y, z));
        }
        tag.listenForInt("Dimension", data::setDimension);
        tag.listenForInt("rainTime", data::setRainTime);
        tag.listenForFloat("rainLevel", data::setRainLevel);
        tag.listenForFloat("thunderLevel", data::setLightningLevel);
        tag.listenForBoolean("hardcore", data::setHardcore);

        CompoundTag gameRulesTag = tag.getCompound("GameRules");
        GameRuleRegistry.get().getRules().forEach(rule -> {
            String value = gameRulesTag.getString(rule.getName());
            if (rule.getValueClass() == Boolean.class) {
                data.getGameRules().put((GameRule<Boolean>) rule, Boolean.valueOf(value));
            } else if (rule.getValueClass() == Integer.class) {
                data.getGameRules().put((GameRule<Integer>) rule, Integer.valueOf(value));
            } else if (rule.getValueClass() == Float.class) {
                data.getGameRules().put((GameRule<Float>) rule, Float.valueOf(value));
            }
        });
    }
}
