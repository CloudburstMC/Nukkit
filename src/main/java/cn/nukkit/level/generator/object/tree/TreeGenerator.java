package cn.nukkit.level.generator.object.tree;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.Level;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3i;

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

    public void generateSaplings(Level level, Random random, Vector3i pos) {
    }

    protected void setDirtAt(ChunkManager level, Vector3i pos) {
        setDirtAt(level, pos.getX(), pos.getY(), pos.getZ());
    }

    /*
     * sets dirt at a specific location if it isn't already dirt
     */
    protected void setDirtAt(ChunkManager level, int x, int y, int z) {
        if (level.getBlockId(x, y, z) != DIRT) {
            level.setBlockId(x, y, z, DIRT);
        }
    }
}
