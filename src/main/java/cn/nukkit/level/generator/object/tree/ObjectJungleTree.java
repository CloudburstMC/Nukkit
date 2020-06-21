package cn.nukkit.level.generator.object.tree;

import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockWood;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.NukkitRandom;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ObjectJungleTree extends ObjectTree {

    private int treeHeight = 8;

    @Override
    public int getType() {
        return BlockWood.JUNGLE;
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
        this.treeHeight = random.nextBoundedInt(6) + 4;
        super.placeObject(level, x, y, z, random);
    }

}
