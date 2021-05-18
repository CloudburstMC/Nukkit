package cn.nukkit.world.generator.populator.impl;

import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.world.ChunkManager;
import cn.nukkit.world.format.FullChunk;
import cn.nukkit.world.generator.object.ore.OreType;
import cn.nukkit.world.generator.populator.type.Populator;

/**
 * @author DaPorkchop_
 */
public class PopulatorOre extends Populator {
    private final int replaceId;
    private final OreType[] oreTypes;

    public PopulatorOre(int replaceId, OreType[] oreTypes) {
        this.replaceId = replaceId;
        this.oreTypes = oreTypes;
    }

    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        int sx = chunkX << 4;
        int ex = sx + 15;
        int sz = chunkZ << 4;
        int ez = sz + 15;
        for (OreType type : this.oreTypes) {
            for (int i = 0; i < type.clusterCount; i++) {
                int x = NukkitMath.randomRange(random, sx, ex);
                int z = NukkitMath.randomRange(random, sz, ez);
                int y = NukkitMath.randomRange(random, type.minHeight, type.maxHeight);
                if (level.getBlockIdAt(x, y, z) != replaceId) {
                    continue;
                }
                type.spawn(level, random, replaceId, x, y, z);
            }
        }
    }
}
