package cn.nukkit.world.generator.object.tree;

import cn.nukkit.math.NukkitRandom;
import cn.nukkit.world.ChunkManager;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ObjectTallBirchTree extends ObjectBirchTree {

    @Override
    public void placeObject(ChunkManager level, int x, int y, int z, NukkitRandom random) {
        this.treeHeight = random.nextBoundedInt(3) + 10;
        super.placeObject(level, x, y, z, random);
    }
}
