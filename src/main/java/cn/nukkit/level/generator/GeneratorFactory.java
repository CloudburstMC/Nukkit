package cn.nukkit.level.generator;

import cn.nukkit.math.BedrockRandom;

import java.util.Map;

@FunctionalInterface
public interface GeneratorFactory {

    Generator create(BedrockRandom random, Map<String, Object> options);
}
