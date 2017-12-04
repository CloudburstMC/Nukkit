package cn.nukkit.server.level.generator.object.tree;

import cn.nukkit.server.block.Block;
import cn.nukkit.server.block.BlockSapling;
import cn.nukkit.server.level.ChunkManager;
import cn.nukkit.server.math.NukkitRandom;

import java.util.HashMap;
import java.util.Map;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class ObjectTree {
    public final Map<Integer, Boolean> overridable = new HashMap<Integer, Boolean>() {
        {
            put(Block.AIR, true);
            put(Block.SAPLING, true);
            put(Block.LOG, true);
            put(Block.LEAVES, true);
            put(Block.SNOW_LAYER, true);
            put(Block.LOG2, true);
            put(Block.LEAVES2, true);
        }
    };

    public int getType() {
        return 0;
    }

    public int getTrunkBlock() {
        return Block.LOG;
    }

    public int getLeafBlock() {
        return Block.LEAVES;
    }

    public int getTreeHeight() {
        return 7;
    }

    public static void growTree(ChunkManager level, int x, int y, int z, NukkitRandom random) {
        growTree(level, x, y, z, random, 0);
    }

    public static void growTree(ChunkManager level, int x, int y, int z, NukkitRandom random, int type) {
        ObjectTree tree;
        switch (type) {
            case BlockSapling.SPRUCE:
                if (random.nextBoundedInt(39) == 0) {
                    tree = new ObjectSpruceTree();
                } else {
                    tree = new ObjectSpruceTree();
                }
                break;
            case BlockSapling.BIRCH:
                if (random.nextBoundedInt(39) == 0) {
                    tree = new ObjectTallBirchTree();
                } else {
                    tree = new ObjectBirchTree();
                }
                break;
            case BlockSapling.JUNGLE:
                tree = new ObjectJungleTree();
                break;
            case BlockSapling.OAK:
            default:
                tree = new ObjectOakTree();
                //todo: more complex treeeeeeeeeeeeeeeee
                break;
        }

        if (tree.canPlaceObject(level, x, y, z, random)) {
            tree.placeObject(level, x, y, z, random);
        }
    }


    public boolean canPlaceObject(ChunkManager level, int x, int y, int z, NukkitRandom random) {
        int radiusToCheck = 0;
        for (int yy = 0; yy < this.getTreeHeight() + 3; ++yy) {
            if (yy == 1 || yy == this.getTreeHeight()) {
                ++radiusToCheck;
            }
            for (int xx = -radiusToCheck; xx < (radiusToCheck + 1); ++xx) {
                for (int zz = -radiusToCheck; zz < (radiusToCheck + 1); ++zz) {
                    if (!this.overridable.containsKey(level.getBlockIdAt(x + xx, y + yy, z + zz))) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public void placeObject(ChunkManager level, int x, int y, int z, NukkitRandom random) {

        this.placeTrunk(level, x, y, z, random, this.getTreeHeight() - 1);

        for (int yy = y - 3 + this.getTreeHeight(); yy <= y + this.getTreeHeight(); ++yy) {
            double yOff = yy - (y + this.getTreeHeight());
            int mid = (int) (1 - yOff / 2);
            for (int xx = x - mid; xx <= x + mid; ++xx) {
                int xOff = Math.abs(xx - x);
                for (int zz = z - mid; zz <= z + mid; ++zz) {
                    int zOff = Math.abs(zz - z);
                    if (xOff == mid && zOff == mid && (yOff == 0 || random.nextBoundedInt(2) == 0)) {
                        continue;
                    }
                    if (!Block.solid[level.getBlockIdAt(xx, yy, zz)]) {

                        level.setBlockIdAt(xx, yy, zz, this.getLeafBlock());
                        level.setBlockDataAt(xx, yy, zz, this.getType());
                    }
                }
            }
        }
    }

    protected void placeTrunk(ChunkManager level, int x, int y, int z, NukkitRandom random, int trunkHeight) {
        // The base dirt block
        level.setBlockIdAt(x, y - 1, z, Block.DIRT);

        for (int yy = 0; yy < trunkHeight; ++yy) {
            int blockId = level.getBlockIdAt(x, y + yy, z);
            if (this.overridable.containsKey(blockId)) {
                level.setBlockIdAt(x, y + yy, z, this.getTrunkBlock());
                level.setBlockDataAt(x, y + yy, z, this.getType());
            }
        }
    }
}
