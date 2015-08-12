package cn.nukkit.level.generator.normal;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.math.Vector3;

import java.util.Random;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Normal extends Generator {
    public Normal() {
        this(new Object[]{});
    }

    public Normal(Object[] options) {
        super(options);
    }

    @Override
    public void init(ChunkManager level, Random random) {
        //todo
    }

    @Override
    public void generateChunk(int chunkX, int chunkZ) {
        //todo
    }

    @Override
    public void populateChunk(int chunkX, int chunkZ) {
        //todo
    }

    @Override
    public Object[] getSettings() {
        return new Object[0];
    }

    @Override
    public String getName() {
        return "normal";
    }

    @Override
    public Vector3 getSpawn() {
        return null;
    }
}
