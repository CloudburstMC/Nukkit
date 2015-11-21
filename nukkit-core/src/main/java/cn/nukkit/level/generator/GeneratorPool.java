package cn.nukkit.level.generator;

import java.util.HashMap;
import java.util.Map;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class GeneratorPool {
    private static Map<Integer, Generator> generators = new HashMap<>();

    public static void put(int levelId, Generator generator) {
        generators.put(levelId, generator);
    }

    public static void remove(int levelId) {
        generators.remove(levelId);
    }

    public static boolean exists(int levelId) {
        return generators.containsKey(levelId);
    }

    public static Generator get(int levelId) {
        return generators.getOrDefault(levelId, null);
    }
}
