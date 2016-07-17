package cn.nukkit.level.generator.task;

import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.level.SimpleChunkManager;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.level.generator.biome.Biome;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.scheduler.AsyncTask;

import java.util.Map;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class GeneratorRegisterTask extends AsyncTask {

    public final Class<? extends Generator> generator;
    public final Map<String, Object> settings;
    public final long seed;
    public final int levelId;

    public GeneratorRegisterTask(Level level, Generator generator) {
        this.generator = generator.getClass();
        this.settings = generator.getSettings();
        this.seed = level.getSeed();
        this.levelId = level.getId();
    }

    @Override
    public void onRun() {
        Block.init();
        Biome.init();
        SimpleChunkManager manager = new SimpleChunkManager(this.seed);
        try {
            Generator generator = this.generator.getConstructor(Map.class).newInstance(this.settings);
            generator.init(manager, new NukkitRandom(manager.getSeed()));
            GeneratorPool.put(this.levelId, generator);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
