package cn.nukkit.level;

import java.util.HashMap;
import java.util.Map;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ChunkManagerPool {

    private static Map<Integer, ChunkManager> managers = new HashMap<>();

    public static void put(int levelId, ChunkManager manager) {
        managers.put(levelId, manager);
    }

    public static void remove(int levelId) {
        managers.remove(levelId);
    }

    public static boolean exists(int levelId) {
        return managers.containsKey(levelId);
    }

    public static ChunkManager get(int levelId) {
        return managers.getOrDefault(levelId, null);
    }
}
