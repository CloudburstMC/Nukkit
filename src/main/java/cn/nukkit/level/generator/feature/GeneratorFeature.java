package cn.nukkit.level.generator.feature;

import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.math.BedrockRandom;

public interface GeneratorFeature extends Feature {

    void generate(BedrockRandom random, IChunk chunk);
}
