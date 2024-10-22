package cn.nukkit.level.generator.object.tree;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.NukkitRandom;

public class ObjectFallenTree {

    public ObjectFallenTree(ChunkManager level, int x, int y, int z, int type, NukkitRandom random) {
        placeObject(level, x, y, z, type, random);
    }

    public void placeObject(ChunkManager level, int x, int y, int z, int type, NukkitRandom random) {
        int direction = random.nextRange(1, 2);
        if (direction == 1 && type == 0) { //oak tree
            //tree
            level.setBlockAt(x - 1, y, z, Block.WOOD);
            level.setBlockAt(x + 1, y, z, Block.WOOD, 12);
            level.setBlockAt(x + 2, y, z, Block.WOOD, 12);
            //vines
            level.setBlockAt(x - 2, y, z, Block.VINES, 8);
            level.setBlockAt(x - 1, y, z + 1, Block.VINES, 4);
            level.setBlockAt(x - 1, y, z - 1, Block.VINES, 1);
        } else if (direction == 1 && type == 1) { //birch
            //tree
            level.setBlockAt(x - 1, y, z, Block.WOOD, 2);
            level.setBlockAt(x + 1, y, z, Block.WOOD, 14);
            level.setBlockAt(x + 2, y, z, Block.WOOD, 14);
            level.setBlockAt(x + 3, y, z, Block.WOOD, 14);
            //vines
            level.setBlockAt(x - 2, y, z, Block.VINES, 8);
            level.setBlockAt(x - 1, y, z + 1, Block.VINES, 4);
            level.setBlockAt(x - 1, y, z - 1, Block.VINES, 1);
            //mushrooms
            level.setBlockAt(x + 1, y + 1, z, Block.BROWN_MUSHROOM);
            level.setBlockAt(x + 3, y + 1, z, Block.RED_MUSHROOM);
        } else if (direction == 2 && type == 0) { //oak tree
            //tree
            level.setBlockAt(x, y, z - 1, Block.WOOD);
            level.setBlockAt(x, y, z + 1, Block.WOOD, 12);
            level.setBlockAt(x, y, z + 2, Block.WOOD, 12);
            //vines
            level.setBlockAt(x, y, z - 2, Block.VINES, 1);
            level.setBlockAt(x + 1, y, z - 1, Block.VINES, 2);
            level.setBlockAt(x - 1, y, z - 1, Block.VINES, 8);
        } else if (direction == 2 && type == 1) { //birch
            //tree
            level.setBlockAt(x, y, z - 1, Block.WOOD, 2);
            level.setBlockAt(x, y, z + 1, Block.WOOD, 14);
            level.setBlockAt(x, y, z + 2, Block.WOOD, 14);
            level.setBlockAt(x, y, z + 3, Block.WOOD, 14);
            //vines
            level.setBlockAt(x, y, z - 2, Block.VINES, 1);
            level.setBlockAt(x + 1, y, z - 1, Block.VINES, 2);
            level.setBlockAt(x - 1, y, z - 1, Block.VINES, 8);
            //mushrooms
            level.setBlockAt(x, y + 1, z + 1, Block.BROWN_MUSHROOM);
            level.setBlockAt(x, y + 1, z + 3, Block.RED_MUSHROOM);
        }
    }
}
