package cn.nukkit.level.generator.object.tree;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockSapling;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.NukkitRandom;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class ObjectTree {

    public static void growTree(final ChunkManager level, final int x, final int y, final int z, final NukkitRandom random) {
        ObjectTree.growTree(level, x, y, z, random, 0);
    }

    public static void growTree(final ChunkManager level, final int x, final int y, final int z, final NukkitRandom random, final int type) {
        final ObjectTree tree;
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
                //todo: more complex treeeeeeeeeeeeeeeee
                break;
        }

        if (tree.canPlaceObject(level, x, y, z, random)) {
            tree.placeObject(level, x, y, z, random);
        }
    }

    public int getType() {
        return 0;
    }

    public int getTrunkBlock() {
        return BlockID.LOG;
    }

    public int getLeafBlock() {
        return BlockID.LEAVES;
    }

    public int getTreeHeight() {
        return 7;
    }

    public boolean canPlaceObject(final ChunkManager level, final int x, final int y, final int z, final NukkitRandom random) {
        int radiusToCheck = 0;
        for (int yy = 0; yy < this.getTreeHeight() + 3; ++yy) {
            if (yy == 1 || yy == this.getTreeHeight()) {
                ++radiusToCheck;
            }
            for (int xx = -radiusToCheck; xx < radiusToCheck + 1; ++xx) {
                for (int zz = -radiusToCheck; zz < radiusToCheck + 1; ++zz) {
                    if (!this.overridable(level.getBlockIdAt(x + xx, y + yy, z + zz))) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public void placeObject(final ChunkManager level, final int x, final int y, final int z, final NukkitRandom random) {

        this.placeTrunk(level, x, y, z, random, this.getTreeHeight() - 1);

        for (int yy = y - 3 + this.getTreeHeight(); yy <= y + this.getTreeHeight(); ++yy) {
            final double yOff = yy - (y + this.getTreeHeight());
            final int mid = (int) (1 - yOff / 2);
            for (int xx = x - mid; xx <= x + mid; ++xx) {
                final int xOff = Math.abs(xx - x);
                for (int zz = z - mid; zz <= z + mid; ++zz) {
                    final int zOff = Math.abs(zz - z);
                    if (xOff == mid && zOff == mid && (yOff == 0 || random.nextBoundedInt(2) == 0)) {
                        continue;
                    }
                    if (!Block.solid[level.getBlockIdAt(xx, yy, zz)]) {
                        level.setBlockAt(xx, yy, zz, this.getLeafBlock(), this.getType());
                    }
                }
            }
        }
    }

    protected boolean overridable(final int id) {
        switch (id) {
            case BlockID.AIR:
            case BlockID.SAPLING:
            case BlockID.LOG:
            case BlockID.LEAVES:
            case BlockID.SNOW_LAYER:
            case BlockID.LOG2:
            case BlockID.LEAVES2:
                return true;
            default:
                return false;
        }
    }

    protected void placeTrunk(final ChunkManager level, final int x, final int y, final int z, final NukkitRandom random, final int trunkHeight) {
        // The base dirt block
        level.setBlockAt(x, y - 1, z, BlockID.DIRT);

        for (int yy = 0; yy < trunkHeight; ++yy) {
            final int blockId = level.getBlockIdAt(x, y + yy, z);
            if (this.overridable(blockId)) {
                level.setBlockAt(x, y + yy, z, this.getTrunkBlock(), this.getType());
            }
        }
    }

}
