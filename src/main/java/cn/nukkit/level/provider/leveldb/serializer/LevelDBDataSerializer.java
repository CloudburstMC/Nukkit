package cn.nukkit.level.provider.leveldb.serializer;

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
import com.nukkitx.nbt.stream.LittleEndianDataInputStream;
import com.nukkitx.nbt.stream.LittleEndianDataOutputStream;
import com.nukkitx.nbt.stream.NBTInputStream;
import com.nukkitx.nbt.stream.NBTOutputStream;
import com.nukkitx.nbt.tag.CompoundTag;
import com.nukkitx.nbt.tag.Tag;
import lombok.extern.log4j.Log4j2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;

@Log4j2
@SuppressWarnings("UnstableApiUsage")
public class LevelDBDataSerializer implements LevelDataSerializer {
    public static final LevelDataSerializer INSTANCE = new LevelDBDataSerializer();

    private static final TypeReference<Map<String, Object>> OPTIONS_TYPE = new TypeReference<Map<String, Object>>() {};
    private static final int VERSION = 8;

    @Override
    public LoadState load(LevelData data, Path levelPath, String levelId) throws IOException {
        Path levelDatPath = levelPath.resolve("level.dat");
        Path levelDatOldPath = levelPath.resolve("level.dat_old");

        if (Files.notExists(levelDatPath) && Files.notExists(levelDatOldPath)) {
            return LoadState.NOT_FOUND;
        }

        try {
            loadData(data, levelDatPath);
        } catch (IOException e) {
            // Attempt to load backup
            log.warn("Unable to load level.dat file, attempting to load backup.");
            loadData(data, levelDatOldPath);
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
                .stringTag("FlatWorldLayers", data.getGeneratorOptions())
                .stringTag("generatorName", data.getGenerator().toString())
                .intTag("lightningTime", data.getLightningTime())
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
                .floatTag("lightningLevel", data.getLightningLevel())
                .booleanTag("hardcore", data.isHardcore());

        // Gamerules - No idea why these aren't in a separate tag
        GameRuleMap gameRules = data.getGameRules();
        gameRules.forEach((gameRule, o) -> {
            String name = gameRule.getName().toLowerCase();
            if (gameRule.getValueClass() == Boolean.class) {
                tag.booleanTag(name, (boolean) o);
            } else if (gameRule.getValueClass() == Integer.class) {
                tag.intTag(name, (int) o);
            } else if (gameRule.getValueClass() == Boolean.class) {
                tag.floatTag(name, (float) o);
            }
        });

        byte[] tagBytes;
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream();
             NBTOutputStream nbtOutputStream = NbtUtils.createWriterLE(stream)) {
            nbtOutputStream.write(tag.buildRootTag());
            tagBytes = stream.toByteArray();
        }

        // Write
        try (LittleEndianDataOutputStream stream = new LittleEndianDataOutputStream(Files.newOutputStream(levelDatPath))) {
            stream.writeInt(VERSION);
            stream.writeInt(tagBytes.length);
            stream.write(tagBytes);
        }
    }

    private void loadData(LevelData data, Path levelDatPath) throws IOException {
        CompoundTag tag;
        try (LittleEndianDataInputStream stream = new LittleEndianDataInputStream(Files.newInputStream(levelDatPath));
             NBTInputStream nbtInputStream = new NBTInputStream(stream)) {

            int version = stream.readInt();
            if (version != VERSION) {
                throw new IOException("Incompatible level.dat version");
            }
            stream.readInt(); // Size
            tag = (CompoundTag) nbtInputStream.readTag();
        }

        /*tag.listenForString("LevelName", data::setName);
        if (tag.contains("FlatWorldLayers")) {
            data.setGeneratorOptions(tag.getString("FlatWorldLayers"));
        }
        tag.listenForString("generatorName", s -> data.setGenerator(Identifier.fromString(s)));*/
        tag.listenForInt("lightningTime", data::setLightningTime);
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
        tag.listenForFloat("lightningLevel", data::setLightningLevel);
        tag.listenForBoolean("Hardcore", data::setHardcore);

        GameRuleRegistry.get().getRules().forEach(rule -> {
            Tag<?> gameRuleTag = tag.get(rule.getName().toLowerCase());
            Object value = gameRuleTag == null ? null : gameRuleTag.getValue();
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
