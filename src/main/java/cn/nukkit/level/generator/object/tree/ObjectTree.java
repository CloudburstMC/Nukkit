package cn.nukkit.level.generator.object.tree;

import cn.nukkit.block.BlockIds;
import cn.nukkit.block.BlockSapling;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.BedrockRandom;
import cn.nukkit.registry.BlockRegistry;
import cn.nukkit.utils.Identifier;

import static cn.nukkit.block.BlockIds.*;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class ObjectTree {
    public static void growTree(ChunkManager level, int x, int y, int z, BedrockRandom random, int type) {
        ObjectTree tree;
        switch (type) {
            case BlockSapling.SPRUCE:
                tree = new ObjectSpruceTree();
                break;
            case BlockSapling.BIRCH:
                tree = new ObjectBirchTree();
                break;
            case BlockSapling.JUNGLE:
                tree = new ObjectJungleTree();
                break;
            case BlockSapling.BIRCH_TALL:
                tree = new ObjectTallBirchTree();
                break;
            case BlockSapling.OAK:
            default:
                tree = new ObjectOakTree();
                //todo: more complex tree
                break;
        }

        if (tree.canPlaceObject(level, x, y, z, random)) {
            tree.placeObject(level, x, y, z, random);
        }
    }

    public int getType() {
        return 0;
    }

    protected boolean overridable(Identifier id) {
        return id == AIR || id == SAPLING || id == LOG || id == LOG2 || id == LEAVES || id == LEAVES2 || id == SNOW_LAYER;
    }

    public Identifier getTrunkBlock() {
        return LOG;
    }

    public int getTreeHeight() {
        return 7;
    }

    public static void growTree(ChunkManager level, int x, int y, int z, BedrockRandom random) {
        growTree(level, x, y, z, random, 0);
    }

    public Identifier getLeafBlock() {
        return LEAVES;
    }


    public boolean canPlaceObject(ChunkManager level, int x, int y, int z, BedrockRandom random) {
        int radiusToCheck = 0;
        for (int yy = 0; yy < this.getTreeHeight() + 3; ++yy) {
            if (yy == 1 || yy == this.getTreeHeight()) {
                ++radiusToCheck;
            }
            for (int xx = -radiusToCheck; xx < (radiusToCheck + 1); ++xx) {
                for (int zz = -radiusToCheck; zz < (radiusToCheck + 1); ++zz) {
                    if (!this.overridable(level.getBlockId(x + xx, y + yy, z + zz))) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public void placeObject(ChunkManager level, int x, int y, int z, BedrockRandom random) {

        this.placeTrunk(level, x, y, z, random, this.getTreeHeight() - 1);

        for (int yy = y - 3 + this.getTreeHeight(); yy <= y + this.getTreeHeight(); ++yy) {
            double yOff = yy - (y + this.getTreeHeight());
            int mid = (int) (1 - yOff / 2);
            for (int xx = x - mid; xx <= x + mid; ++xx) {
                int xOff = Math.abs(xx - x);
                for (int zz = z - mid; zz <= z + mid; ++zz) {
                    int zOff = Math.abs(zz - z);
                    if (xOff == mid && zOff == mid && (yOff == 0 || random.nextInt(2) == 0)) {
                        continue;
                    }
                    if (!BlockRegistry.get().getBlock(level.getBlockId(xx, yy, zz), 0).isSolid()) {
                        level.setBlockAt(xx, yy, zz, this.getLeafBlock(), this.getType());
                    }
                }
            }
        }
    }

    protected void placeTrunk(ChunkManager level, int x, int y, int z, BedrockRandom random, int trunkHeight) {
        // The base dirt block
        level.setBlockId(x, y - 1, z, BlockIds.DIRT);

        for (int yy = 0; yy < trunkHeight; ++yy) {
            Identifier blockId = level.getBlockId(x, y + yy, z);
            if (this.overridable(blockId)) {
                level.setBlockAt(x, y + yy, z, this.getTrunkBlock(), this.getType());
            }
        }
    }
}
