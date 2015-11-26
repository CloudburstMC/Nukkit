package cn.nukkit.level.generator.object.tree;

import cn.nukkit.block.Block;
import cn.nukkit.block.Wood;
import cn.nukkit.level.ChunkManager;

import java.util.Random;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class JungleTree extends ObjectTree {
    private int treeHeight = 8;

    @Override
    public int getTrunkBlock() {
        return Block.LOG;
    }

    @Override
    public int getLeafBlock() {
        return Block.LEAVES;
    }

    @Override
    public int getType() {
        return Wood.JUNGLE;
    }

    @Override
    public int getTreeHeight() {
        return this.treeHeight;
    }

    @Override
    public void placeObject(ChunkManager level, int x, int y, int z, Random random) {
        this.treeHeight = random.nextInt(6) + 4;
        super.placeObject(level, x, y, z, random);
    }
}
