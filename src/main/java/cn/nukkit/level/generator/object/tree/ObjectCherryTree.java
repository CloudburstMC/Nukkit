package cn.nukkit.level.generator.object.tree;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.utils.Utils;

public class ObjectCherryTree extends ObjectTree {

    @Override
    protected boolean overridable(int id) {
        switch (id) {
            case BlockID.AIR:
            case BlockID.SNOW_LAYER:
            case BlockID.CHERRY_LOG:
            case BlockID.CHERRY_LEAVES:
                return true;
            default:
                return false;
        }
    }

    @Override
    public int getTrunkBlock() {
        return BlockID.CHERRY_LOG;
    }

    @Override
    public int getLeafBlock() {
        return BlockID.CHERRY_LEAVES;
    }

    @Override
    public void placeObject(ChunkManager level, int x, int y, int z, NukkitRandom random) {
        int treeHeight = random.nextBoundedInt(2) + 9;

        int i2 = y + treeHeight;
        int maxBlockY = level.getMaxBlockY();

        if (i2 + 2 >= maxBlockY) {
            return;
        }

        level.setBlockAt(x, y, z, BlockID.AIR);

        for (int il = 0; il < treeHeight + 1; il++) {
            placeLogAt(level, x, il + y, z);
        }

        y++;

        for (int yy = y - 3 + treeHeight; yy <= y + treeHeight; ++yy) {
            double yOff = yy - (y + treeHeight);
            int mid = (int) (1 - yOff / 2) + 1;
            for (int xx = x - mid; xx <= x + mid; ++xx) {
                int xOff = Math.abs(xx - x);
                for (int zz = z - mid; zz <= z + mid; ++zz) {
                    int zOff = Math.abs(zz - z);
                    if (xOff == mid && zOff == mid && (yOff == 0 || random.nextBoundedInt(2) == 0)) {
                        continue;
                    }
                    if (!Block.isBlockSolidById(level.getBlockIdAt(xx, yy - 1, zz))) {
                        level.setBlockAt(xx, yy - 1, zz, this.getLeafBlock(), this.getType());
                    }
                }
            }
        }

        level.setBlockAt(x, i2, z, this.getLeafBlock(), this.getType());

        y--;

        int h = treeHeight >> 1;

        switch (Utils.rand(0, 3)) {
            case 0:
                for (int xp = 0; xp < h + 3; xp++) {
                    x++;
                    level.setBlockAt(x, y + h, z, this.getTrunkBlock(), 1);
                }
                break;
            case 1:
                for (int xp = 0; xp < h + 3; xp++) {
                    x--;
                    level.setBlockAt(x, y + h, z, this.getTrunkBlock(), 1);
                }
                break;
            case 2:
                for (int xp = 0; xp < h + 3; xp++) {
                    z++;
                    level.setBlockAt(x, y + h, z, this.getTrunkBlock(), 2);
                }
                break;
            case 3:
                for (int xp = 0; xp < h + 3; xp++) {
                    z--;
                    level.setBlockAt(x, y + h, z, this.getTrunkBlock(), 2);
                }
                break;
        }

        for (int il = h + 1; il < treeHeight + 1; il++) {
            placeLogAt(level, x, il + y, z);
        }

        y++;

        for (int yy = y - 3 + treeHeight; yy <= y + treeHeight; ++yy) {
            double yOff = yy - (y + treeHeight);
            int mid = (int) (1 - yOff / 2) + 1;
            for (int xx = x - mid; xx <= x + mid; ++xx) {
                int xOff = Math.abs(xx - x);
                for (int zz = z - mid; zz <= z + mid; ++zz) {
                    int zOff = Math.abs(zz - z);
                    if (xOff == mid && zOff == mid && (yOff == 0 || random.nextBoundedInt(2) == 0)) {
                        continue;
                    }
                    if (!Block.isBlockSolidById(level.getBlockIdAt(xx, yy - 1, zz))) {
                        level.setBlockAt(xx, yy - 1, zz, this.getLeafBlock(), this.getType());
                    }
                }
            }
        }

        level.setBlockAt(x, i2, z, this.getLeafBlock(), this.getType());
    }

    private void placeLogAt(ChunkManager level, int x, int y, int z) {
        if (overridable(level.getBlockIdAt(x, y, z))) {
            level.setBlockAt(x, y, z, this.getTrunkBlock(), 0);
        }
    }
}
