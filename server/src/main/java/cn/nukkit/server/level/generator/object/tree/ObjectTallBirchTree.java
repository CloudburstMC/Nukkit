package cn.nukkit.server.level.generator.object.tree;

import cn.nukkit.server.level.ChunkManager;
import cn.nukkit.server.math.NukkitRandom;

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
