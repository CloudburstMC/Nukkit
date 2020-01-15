package cn.nukkit.level.generator.object.tree;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.BedrockRandom;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ObjectTallBirchTree extends ObjectBirchTree {

    @Override
    public void placeObject(ChunkManager level, int x, int y, int z, BedrockRandom random) {
        this.treeHeight = random.nextInt(3) + 10;
        super.placeObject(level, x, y, z, random);
    }
}
