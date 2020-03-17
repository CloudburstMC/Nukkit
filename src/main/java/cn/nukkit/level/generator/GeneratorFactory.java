package cn.nukkit.level.generator;

import cn.nukkit.math.BedrockRandom;

@FunctionalInterface
public interface GeneratorFactory {

    Generator create(BedrockRandom random, String options);
}
