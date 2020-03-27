package cn.nukkit.level.generator.object.tree;

import cn.nukkit.block.BlockLog;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.BedrockRandom;
import cn.nukkit.utils.Identifier;

import static cn.nukkit.block.BlockIds.LEAVES;
import static cn.nukkit.block.BlockIds.LOG;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ObjectBirchTree extends ObjectTree {
    protected int treeHeight = 7;

    @Override
    public Identifier getTrunkBlock() {
        return LOG;
    }

    @Override
    public Identifier getLeafBlock() {
        return LEAVES;
    }

    @Override
    public int getType() {
        return BlockLog.BIRCH;
    }

    @Override
    public int getTreeHeight() {
        return this.treeHeight;
    }

    @Override
    public void placeObject(ChunkManager level, int x, int y, int z, BedrockRandom random) {
        this.treeHeight = random.nextInt(2) + 5;
        super.placeObject(level, x, y, z, random);
    }
}
