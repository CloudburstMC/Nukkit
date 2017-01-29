package cn.nukkit.level.generator.object.tree;

import cn.nukkit.block.BlockDirt;
import cn.nukkit.item.Item;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;

import java.util.Random;

public abstract class TreeGenerator extends cn.nukkit.level.generator.object.BasicGenerator {

    /**
     * returns whether or not a tree can grow into a block
     * For example, a tree will not grow into stone
     */
    protected boolean canGrowInto(int id) {
        return id == Item.AIR || id == Item.LEAVES || id == Item.GRASS || id == Item.DIRT || id == Item.LOG || id == Item.LOG2 || id == Item.SAPLING || id == Item.VINE;
    }

    public void generateSaplings(Level level, Random random, Vector3 pos) {
    }

    protected void setDirtAt(ChunkManager level, BlockVector3 pos) {
        setDirtAt(level, new Vector3(pos.x, pos.y, pos.z));
    }

    /**
     * sets dirt at a specific location if it isn't already dirt
     */
    protected void setDirtAt(ChunkManager level, Vector3 pos) {
        if (level.getBlockIdAt((int) pos.x, (int) pos.y, (int) pos.z) != Item.DIRT) {
            this.setBlockAndNotifyAdequately(level, pos, new BlockDirt());
        }
    }
}
