package cn.nukkit.level.format.generic;

import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.CompoundTag;
import cn.nukkit.nbt.NbtIo;
import cn.nukkit.utils.LevelException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class BaseLevelProvider implements LevelProvider {
    protected Level level;

    protected String path;

    protected CompoundTag levelData;

    public BaseLevelProvider(Level level, String path) throws IOException {
        this.level = level;
        this.path = path;
        File file_path = new File(this.path);
        if (!file_path.exists()) {
            file_path.mkdirs();
        }
        CompoundTag levelData = NbtIo.readCompressed(new FileInputStream(new File(this.getPath() + "level.dat")));
        if (levelData != null) {
            this.levelData = levelData;
        } else {
            throw new LevelException("Invalid level.dat");
        }

        if (!this.levelData.contains("generateName")) {
            this.levelData.putString("generatorName", Generator.getGenerator("DEFAULT").getSimpleName().toLowerCase());
        }
        if (!this.levelData.contains("generatorOptions")) {
            this.levelData.putString("generatorName", Generator.getGenerator("DEFAULT").getSimpleName().toLowerCase());
        }
    }

    @Override
    public String getPath() {
        return path;
    }

    public Server getServer() {
        return this.level.getServer();
    }

    @Override
    public Level getLevel() {
        return level;
    }

    @Override
    public String getName() {
        return this.levelData.getString("LevelName");
    }

    @Override
    public long getTime() {
        return this.levelData.getInt("Time");
    }

    @Override
    public void setTime(int value) {
        this.levelData.putInt("Time", value);
    }

    @Override
    public long getSeed() {
        return this.levelData.getLong("RandomSeed");
    }

    @Override
    public void setSeed(long value) {
        this.levelData.putLong("RandomSeed", value);
    }

    @Override
    public Vector3 getSpawn() {
        return new Vector3(this.levelData.getInt("SpawnX"), this.levelData.getInt("SpawnY"), this.levelData.getInt("SpawnZ"));
    }

    @Override
    public void setSpawn(Vector3 pos) {
        this.levelData.putInt("SpawnX", (int) pos.x);
        this.levelData.putInt("SpawnY", (int) pos.y);
        this.levelData.putInt("SpawnZ", (int) pos.z);
    }

    @Override
    public void doGarbageCollection() throws IOException {

    }

    public CompoundTag getLevelData() {
        return levelData;
    }

    public void saveLevelData() {
        try {
            NbtIo.writeCompressed(this.levelData, new FileOutputStream(this.getPath() + "level.dat"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
