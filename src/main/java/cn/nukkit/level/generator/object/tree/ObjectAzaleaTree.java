package cn.nukkit.level.generator.object.tree;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.NukkitRandom;

public class ObjectAzaleaTree extends ObjectTree {

    @Override
    protected boolean overridable(int id) {
        switch (id) {
            case BlockID.AIR:
            case BlockID.SNOW_LAYER:
            case BlockID.LOG:
            case BlockID.AZALEA:
            case BlockID.FLOWERING_AZALEA:
            case BlockID.AZALEA_LEAVES:
            case BlockID.AZALEA_LEAVES_FLOWERED:
                return true;
            default:
                return false;
        }
    }

    @Override
    public int getLeafBlock() {
        return BlockID.AZALEA_LEAVES;
    }

    @Override
    public void placeObject(ChunkManager level, int x, int y, int z, NukkitRandom random) {
        int treeHeight = random.nextBoundedInt(2) + 2;

        int i2 = y + treeHeight;
        int maxBlockY = level.getMaxBlockY();

        if (i2 + 2 >= maxBlockY) {
            return;
        }

        for (int il = 0; il <= treeHeight + 1; il++) { // +1 to stop leaves decay
            placeLogAt(level, x, il + y, z);
        }

        level.setBlockAt(x, y - 1, z, BlockID.ROOTED_DIRT);

        // Adapted from https://github.com/PowerNukkitX/PowerNukkitX/blob/master/src/main/java/cn/nukkit/level/generator/object/ObjectAzaleaTree.java

        for (int i3 = -2; i3 <= 1; ++i3) {
            for (int l3 = -2; l3 <= 1; ++l3) {
                int k4 = 1;
                int offsetX = random.nextRange(0, 1);
                int offsetY = random.nextRange(0, 1);
                int offsetZ = random.nextRange(0, 1);
                this.placeLeafAt(level, x + i3 + offsetX, i2 + k4 + offsetY, z + l3 + offsetZ, random);
                this.placeLeafAt(level, x - i3 + offsetX, i2 + k4 + offsetY, z + l3 + offsetZ, random);
                this.placeLeafAt(level, x + i3 + offsetX, i2 + k4 + offsetY, z - l3 + offsetZ, random);
                this.placeLeafAt(level, x - i3 + offsetX, i2 + k4 + offsetY, z - l3 + offsetZ, random);

                k4 = 0;
                this.placeLeafAt(level, x + i3, i2 + k4, z + l3, random);
                this.placeLeafAt(level, x - i3, i2 + k4, z + l3, random);
                this.placeLeafAt(level, x + i3, i2 + k4, z - l3, random);
                this.placeLeafAt(level, x - i3, i2 + k4, z - l3, random);

                k4 = 1;
                this.placeLeafAt(level, x + i3, i2 + k4, z + l3, random);
                this.placeLeafAt(level, x - i3, i2 + k4, z + l3, random);
                this.placeLeafAt(level, x + i3, i2 + k4, z - l3, random);
                this.placeLeafAt(level, x - i3, i2 + k4, z - l3, random);

                k4 = 2;
                offsetX = random.nextRange(-1, 0);
                offsetY = random.nextRange(-1, 0);
                offsetZ = random.nextRange(-1, 0);

                this.placeLeafAt(level, x + i3 + offsetX, i2 + k4 + offsetY, z + l3 + offsetZ, random);
                this.placeLeafAt(level, x - i3 + offsetX, i2 + k4 + offsetY, z + l3 + offsetZ, random);
                this.placeLeafAt(level, x + i3 + offsetX, i2 + k4 + offsetY, z - l3 + offsetZ, random);
                this.placeLeafAt(level, x - i3 + offsetX, i2 + k4 + offsetY, z - l3 + offsetZ, random);
            }
        }

        // Always hide trunk
        this.placeLeafAt(level, x, i2 + 2, z, random);
    }

    private void placeLogAt(ChunkManager level, int x, int y, int z) {
        if (overridable(level.getBlockIdAt(x, y, z))) {
            level.setBlockAt(x, y, z, this.getTrunkBlock(), 0);
        }
    }

    private void placeLeafAt(ChunkManager level, int x, int y, int z, NukkitRandom random) {
        if (level.getBlockIdAt(x, y, z) == BlockID.AIR) {
            if (random.nextBoundedInt(3) == 0) {
                level.setBlockAt(x, y, z, BlockID.AZALEA_LEAVES_FLOWERED, 0);
            } else {
                level.setBlockAt(x, y, z, BlockID.AZALEA_LEAVES, 0);
            }
        }
    }
}
