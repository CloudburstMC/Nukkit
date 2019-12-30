package cn.nukkit.level.generator.object;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;

import static cn.nukkit.block.BlockIds.*;

/**
 * author: ItsLucas
 * Nukkit Project
 */
public class ObjectTallGrass {
    public static void growGrass(ChunkManager level, Vector3 pos, NukkitRandom random) {
        for (int i = 0; i < 128; ++i) {
            int num = 0;

            int x = pos.getFloorX();
            int y = pos.getFloorY() + 1;
            int z = pos.getFloorZ();

            while (true) {
                if (num >= i / 16) {
                    if (level.getBlockIdAt(x, y, z) == AIR) {
                        if (random.nextBoundedInt(8) == 0) {
                            //porktodo: biomes have specific flower types that can grow in them
                            if (random.nextBoolean()) {
                                level.setBlockIdAt(x, y, z, YELLOW_FLOWER);
                            } else {
                                level.setBlockIdAt(x, y, z, RED_FLOWER);
                            }
                        } else {
                            level.setBlockAt(x, y, z, TALL_GRASS, 1);
                        }
                    }

                    break;
                }

                x += random.nextRange(-1, 1);
                y += random.nextRange(-1, 1) * random.nextBoundedInt(3) / 2;
                z += random.nextRange(-1, 1);

                if (level.getBlockIdAt(x, y - 1, z) != GRASS || y > 255 || y < 0) {
                    break;
                }

                ++num;
            }
        }
    }
}
