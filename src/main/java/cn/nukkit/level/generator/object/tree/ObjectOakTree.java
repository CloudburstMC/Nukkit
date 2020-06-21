package cn.nukkit.level.generator.object.tree;

import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockWood;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.NukkitRandom;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ObjectOakTree extends ObjectTree {

    private int treeHeight = 7;

    @Override
    public int getType() {
        return BlockWood.OAK;
    }

    @Override
    public int getTrunkBlock() {
        return BlockID.LOG;
    }

    @Override
    public int getLeafBlock() {
        return BlockID.LEAVES;
    }

    @Override
    public int getTreeHeight() {
        return this.treeHeight;
    }

    @Override
    public void placeObject(final ChunkManager level, final int x, final int y, final int z, final NukkitRandom random) {
        this.treeHeight = random.nextBoundedInt(3) + 4;
        super.placeObject(level, x, y, z, random);
    }

}
