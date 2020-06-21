package cn.nukkit.level.generator;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.Level;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;
import java.util.HashMap;
import java.util.Map;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class Generator implements BlockID {

    public static final int TYPE_OLD = 0;

    public static final int TYPE_INFINITE = 1;

    public static final int TYPE_FLAT = 2;

    public static final int TYPE_NETHER = 3;

    public static final int TYPE_VOID = 4;

    private static final Map<String, Class<? extends Generator>> nameList = new HashMap<>();

    private static final Map<Integer, Class<? extends Generator>> typeList = new HashMap<>();

    public static boolean addGenerator(final Class<? extends Generator> clazz, String name, final int type) {
        name = name.toLowerCase();
        if (clazz != null && !Generator.nameList.containsKey(name)) {
            Generator.nameList.put(name, clazz);
            if (!Generator.typeList.containsKey(type)) {
                Generator.typeList.put(type, clazz);
            }
            return true;
        }
        return false;
    }

    public static String[] getGeneratorList() {
        final String[] keys = new String[Generator.nameList.size()];
        return Generator.nameList.keySet().toArray(keys);
    }

    public static Class<? extends Generator> getGenerator(String name) {
        name = name.toLowerCase();
        if (Generator.nameList.containsKey(name)) {
            return Generator.nameList.get(name);
        }
        return Normal.class;
    }

    public static Class<? extends Generator> getGenerator(final int type) {
        if (Generator.typeList.containsKey(type)) {
            return Generator.typeList.get(type);
        }
        return Normal.class;
    }

    public static String getGeneratorName(final Class<? extends Generator> c) {
        for (final String key : Generator.nameList.keySet()) {
            if (Generator.nameList.get(key).equals(c)) {
                return key;
            }
        }
        return "unknown";
    }

    public static int getGeneratorType(final Class<? extends Generator> c) {
        for (final int key : Generator.typeList.keySet()) {
            if (Generator.typeList.get(key).equals(c)) {
                return key;
            }
        }
        return Generator.TYPE_INFINITE;
    }

    public abstract int getId();

    public int getDimension() {
        return Level.DIMENSION_OVERWORLD;
    }

    public abstract void init(ChunkManager level, NukkitRandom random);

    public abstract void generateChunk(int chunkX, int chunkZ);

    public abstract void populateChunk(int chunkX, int chunkZ);

    public abstract Map<String, Object> getSettings();

    public abstract String getName();

    public abstract Vector3 getSpawn();

    public abstract ChunkManager getChunkManager();

}
