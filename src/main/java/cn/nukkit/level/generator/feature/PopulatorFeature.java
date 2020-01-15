package cn.nukkit.level.generator.feature;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.BedrockRandom;

public interface PopulatorFeature {

    void populate(ChunkManager level, BedrockRandom random, int chunkX, int chunkZ);
}
