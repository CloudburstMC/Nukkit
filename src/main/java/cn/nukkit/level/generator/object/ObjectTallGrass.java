package cn.nukkit.level.generator.object;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockLayer;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Utils;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author ItsLucas
 * Nukkit Project
 */
public class ObjectTallGrass {

    @Deprecated
    public static void growGrass(ChunkManager level, Vector3 pos, NukkitRandom random) {
        growGrass(level, pos);
    }

    public static void growGrass(ChunkManager level, Vector3 pos) {
        int maxBlockY = level.getMaxBlockY();
        int minBlockY = level.getMinBlockY();

        for (int i = 0; i < 128; ++i) {
            int num = 0;

            int x = pos.getFloorX();
            int y = pos.getFloorY() + 1;
            int z = pos.getFloorZ();

            while (true) {
                if (num >= i >> 4) {
                    if (level.getBlockIdAt(x, y, z) == Block.AIR) {
                        if (ThreadLocalRandom.current().nextInt(8) == 0) {
                            //TODO: biomes have specific flower types that can grow in them
                            if (Utils.rand()) {
                                level.setBlockAt(x, y, z, Block.DANDELION);
                            } else if (Utils.rand()) {
                                level.setBlockAt(x, y, z, Block.TALL_GRASS, 2); // fern
                            } else {
                                level.setBlockAt(x, y, z, Block.POPPY);
                            }
                        } else {
                            level.setBlockAt(x, y, z, Block.TALL_GRASS, 1);
                        }
                    }

                    break;
                }

                x += Utils.rand(-1, 1);
                y += Utils.rand(-1, 1) * ThreadLocalRandom.current().nextInt(3) >> 1;
                z += Utils.rand(-1, 1);

                if (y > maxBlockY || y < minBlockY || level.getBlockIdAt(x, y - 1, z) != Block.GRASS) {
                    break;
                }

                ++num;
            }
        }
    }

    public static void growSeagrass(ChunkManager level, Vector3 pos) {
        int maxBlockY = level.getMaxBlockY();
        int minBlockY = level.getMinBlockY();

        for (int i = 0; i < 48; ++i) {
            int num = 0;

            int x = pos.getFloorX();
            int y = pos.getFloorY() + 1;
            int z = pos.getFloorZ();

            while (true) {
                if (num >= i >> 4) {
                    int block = level.getBlockIdAt(x, y, z);
                    if (block == Block.WATER || block == Block.STILL_WATER) {
                        //if (ThreadLocalRandom.current().nextInt(8) == 0) {
                        // TODO: coral & tall seagrass
                        //} else {
                        level.setBlockAt(x, y, z, Block.SEAGRASS, 0);
                        level.setBlockAtLayer(x, y, z, BlockLayer.WATERLOGGED, Block.WATER, 0);
                        //}
                    }

                    break;
                }

                x += Utils.rand(-1, 1);
                y += Utils.rand(-1, 1) * ThreadLocalRandom.current().nextInt(3) >> 1;
                z += Utils.rand(-1, 1);

                int block;
                if (y > maxBlockY || y < minBlockY || ((block = level.getBlockIdAt(x, y - 1, z)) != Block.DIRT && block != Block.SAND && block != Block.GRAVEL)) {
                    break;
                }

                ++num;
            }
        }
    }
}
