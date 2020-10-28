package cn.nukkit.level.generator.object.tree;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockSapling;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.Position;
import cn.nukkit.math.NukkitRandom;

import java.util.List;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class ObjectTree {
    protected boolean overridable(int id) {
        switch (id) {
            case Block.AIR:
            case Block.SAPLING:
            case Block.LOG:
            case Block.LEAVES:
            case Block.SNOW_LAYER:
            case Block.LOG2:
            case Block.LEAVES2:
                return true;
            default:
                return false;
        }
    }

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

    public static void growTree(ChunkManager level, List<Block> blocks, int x, int y, int z, NukkitRandom random) {
        growTree(level, blocks, x, y, z, random, 0);
    }

    public static void growTree(ChunkManager level, List<Block> blocks, int x, int y, int z, NukkitRandom random, int type) {
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
                //todo: more complex treeeeeeeeeeeeeeeee
                break;
        }

        if (tree.canPlaceObject(level, x, y, z, random)) {
            tree.placeObject(level, blocks, x, y, z, random);
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
                    if (!this.overridable(level.getBlockIdAt(x + xx, y + yy, z + zz))) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public void placeObject(ChunkManager level, List<Block> blocks, int x, int y, int z, NukkitRandom random) {
        this.placeTrunk(level, blocks, x, y, z, random, this.getTreeHeight() - 1);

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
                        blocks.add(Block.get(this.getLeafBlock(), this.getType(), new Position(xx, yy, zz)));
                    }
                }
            }
        }
    }

    protected void placeTrunk(ChunkManager level, List<Block> blocks, int x, int y, int z, NukkitRandom random, int trunkHeight) {
        // The base dirt block
        blocks.add(Block.get(Block.DIRT, 0, new Position(x, y - 1, z)));

        for (int yy = 0; yy < trunkHeight; ++yy) {
            int blockId = level.getBlockIdAt(x, y + yy, z);
            if (this.overridable(blockId)) {
                blocks.add(Block.get(this.getTrunkBlock(), this.getType(), new Position(x, y + yy, z)));
            }
        }
    }
}
