package cn.nukkit.level.generator.populator;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.generator.object.ore.ObjectOre;
import cn.nukkit.level.generator.object.ore.OreType;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.NukkitRandom;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class PopulatorOre extends Populator {
    private final int replaceId;
    private OreType[] oreTypes = new OreType[0];

    public PopulatorOre() {
        this(Block.STONE);
    }

    public PopulatorOre(int id) {
        this.replaceId = id;
    }

    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random) {
        for (OreType type : this.oreTypes) {
            ObjectOre ore = new ObjectOre(random, type, replaceId);
            for (int i = 0; i < ore.type.clusterCount; ++i) {
                int x = NukkitMath.randomRange(random, chunkX << 4, (chunkX << 4) + 15);
                int y = NukkitMath.randomRange(random, ore.type.minHeight, ore.type.maxHeight);
                int z = NukkitMath.randomRange(random, chunkZ << 4, (chunkZ << 4) + 15);
                if (ore.canPlaceObject(level, x, y, z)) {
                    ore.placeObject(level, x, y, z);
                }
            }
        }
    }

    public void setOreTypes(OreType[] oreTypes) {
        this.oreTypes = oreTypes;
    }
}
