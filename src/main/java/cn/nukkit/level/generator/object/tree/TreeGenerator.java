package cn.nukkit.level.generator.object.tree;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Identifier;

import java.util.Random;

import static cn.nukkit.block.BlockIds.*;

public abstract class TreeGenerator extends cn.nukkit.level.generator.object.BasicGenerator {

    /*
     * returns whether or not a tree can grow into a block
     * For example, a tree will not grow into stone
     */
    protected boolean canGrowInto(Identifier id) {
        return id == AIR || id == LEAVES || id == LEAVES2 || id == GRASS || id == DIRT || id == LOG || id == LOG2 ||
                id == SAPLING || id == VINE;
    }

    public void generateSaplings(Level level, Random random, Vector3 pos) {
    }

    protected void setDirtAt(ChunkManager level, BlockVector3 pos) {
        setDirtAt(level, new Vector3(pos.x, pos.y, pos.z));
    }

    /*
     * sets dirt at a specific location if it isn't already dirt
     */
    protected void setDirtAt(ChunkManager level, Vector3 pos) {
        if (level.getBlockIdAt((int) pos.x, (int) pos.y, (int) pos.z) != DIRT) {
            this.setBlockAndNotifyAdequately(level, pos, Block.get(DIRT));
        }
    }
}
