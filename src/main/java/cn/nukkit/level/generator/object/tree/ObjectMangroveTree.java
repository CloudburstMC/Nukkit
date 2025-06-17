package cn.nukkit.level.generator.object.tree;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.NukkitRandom;

public class ObjectMangroveTree extends ObjectTree {

    @Override
    protected boolean overridable(int id) {
        switch (id) {
            case BlockID.AIR:
            case BlockID.SNOW_LAYER:
            case BlockID.MANGROVE_LOG:
            case BlockID.MANGROVE_PROPAGULE:
            case BlockID.MANGROVE_LEAVES:
            case BlockID.MANGROVE_ROOTS:
                return true;
            default:
                return false;
        }
    }

    @Override
    public int getTrunkBlock() {
        return BlockID.MANGROVE_LOG;
    }

    @Override
    public int getLeafBlock() {
        return BlockID.MANGROVE_LEAVES;
    }

    @Override
    public void placeObject(ChunkManager level, int x, int y, int z, NukkitRandom random) {
        int treeHeight = random.nextBoundedInt(3) + 8;

        int i2 = y + treeHeight;
        int maxBlockY = level.getMaxBlockY();

        if (i2 + 2 >= maxBlockY) {
            return;
        }

        level.setBlockAt(x, y, z, BlockID.AIR);

        // Adapted from https://github.com/PowerNukkitX/PowerNukkitX/blob/master/src/main/java/cn/nukkit/level/generator/object/ObjectMangroveTree.java

        for (int il = 0; il <= treeHeight + 1; il++) { // +1 to stop leaves decay
            if (il > 2) {
                placeLogAt(level, x, il + y, z);
            } else {
                placeRootAt(level, x + 1, il + y, z);
                placeRootAt(level, x - 1, il + y, z);
                placeRootAt(level, x, il + y, z + 1);
                placeRootAt(level, x, il + y, z - 1);
            }
        }

        placeRootAt(level, x + 2, y, z);
        placeRootAt(level, x - 2, y, z);
        placeRootAt(level, x, y, z + 2);
        placeRootAt(level, x, y, z - 2);

        for (int i3 = -2; i3 <= 1; ++i3) {
            for (int l3 = -2; l3 <= 1; ++l3) {
                int k4 = 1;
                int offsetX = random.nextRange(0, 1);
                int offsetY = random.nextRange(0, 1);
                int offsetZ = random.nextRange(0, 1);
                this.placeLeafAt(level, x + i3 + offsetX, i2 + k4 + offsetY, z + l3 + offsetZ);
                this.placeLeafAt(level, x - i3 + offsetX, i2 + k4 + offsetY, z + l3 + offsetZ);
                this.placeLeafAt(level, x + i3 + offsetX, i2 + k4 + offsetY, z - l3 + offsetZ);
                this.placeLeafAt(level, x - i3 + offsetX, i2 + k4 + offsetY, z - l3 + offsetZ);

                k4 = 0;
                this.placeLeafAt(level, x + i3, i2 + k4, z + l3);
                this.placeLeafAt(level, x - i3, i2 + k4, z + l3);
                this.placeLeafAt(level, x + i3, i2 + k4, z - l3);
                this.placeLeafAt(level, x - i3, i2 + k4, z - l3);

                k4 = 1;
                this.placeLeafAt(level, x + i3, i2 + k4, z + l3);
                this.placeLeafAt(level, x - i3, i2 + k4, z + l3);
                this.placeLeafAt(level, x + i3, i2 + k4, z - l3);
                this.placeLeafAt(level, x - i3, i2 + k4, z - l3);

                k4 = 2;
                offsetX = random.nextRange(-1, 0);
                offsetY = random.nextRange(-1, 0);
                offsetZ = random.nextRange(-1, 0);

                this.placeLeafAt(level, x + i3 + offsetX, i2 + k4 + offsetY, z + l3 + offsetZ);
                this.placeLeafAt(level, x - i3 + offsetX, i2 + k4 + offsetY, z + l3 + offsetZ);
                this.placeLeafAt(level, x + i3 + offsetX, i2 + k4 + offsetY, z - l3 + offsetZ);
                this.placeLeafAt(level, x - i3 + offsetX, i2 + k4 + offsetY, z - l3 + offsetZ);
            }
        }

        // Always hide trunk
        this.placeLeafAt(level, x, i2 + 2, z);
    }

    private void placeLogAt(ChunkManager level, int x, int y, int z) {
        if (overridable(level.getBlockIdAt(x, y, z))) {
            level.setBlockAt(x, y, z, this.getTrunkBlock(), 0);
        }
    }

    private void placeRootAt(ChunkManager level, int x, int y, int z) {
        if (overridable(level.getBlockIdAt(x, y, z))) {
            level.setBlockAt(x, y, z, BlockID.MANGROVE_ROOTS, 0);
        }
    }

    private void placeLeafAt(ChunkManager level, int x, int y, int z) {
        if (level.getBlockIdAt(x, y, z) == BlockID.AIR) {
            level.setBlockAt(x, y, z, this.getLeafBlock(), 0);
        }
    }
}
