package cn.nukkit.server.level.generator.object.tree;

import cn.nukkit.server.block.BlockDirt;
import cn.nukkit.server.item.Item;
import cn.nukkit.server.level.ChunkManager;
import cn.nukkit.server.level.NukkitLevel;
import cn.nukkit.server.level.generator.object.BasicGenerator;
import cn.nukkit.server.math.BlockVector3;
import cn.nukkit.server.math.Vector3;

import java.util.Random;

public abstract class TreeGenerator extends BasicGenerator {

    /**
     * returns whether or not a tree can grow into a block
     * For example, a tree will not grow into stone
     */
    protected boolean canGrowInto(int id) {
        return id == Item.AIR || id == Item.LEAVES || id == Item.GRASS || id == Item.DIRT || id == Item.LOG || id == Item.LOG2 || id == Item.SAPLING || id == Item.VINE;
    }

    public void generateSaplings(NukkitLevel level, Random random, Vector3 pos) {
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
