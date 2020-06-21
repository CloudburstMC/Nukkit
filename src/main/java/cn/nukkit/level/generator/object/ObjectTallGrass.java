package cn.nukkit.level.generator.object;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;

/**
 * author: ItsLucas
 * Nukkit Project
 */
public class ObjectTallGrass {

    public static void growGrass(final ChunkManager level, final Vector3 pos, final NukkitRandom random) {
        for (int i = 0; i < 128; ++i) {
            int num = 0;

            int x = pos.getFloorX();
            int y = pos.getFloorY() + 1;
            int z = pos.getFloorZ();

            while (true) {
                if (num >= i / 16) {
                    if (level.getBlockIdAt(x, y, z) == BlockID.AIR) {
                        if (random.nextBoundedInt(8) == 0) {
                            //porktodo: biomes have specific flower types that can grow in them
                            if (random.nextBoolean()) {
                                level.setBlockAt(x, y, z, BlockID.DANDELION);
                            } else {
                                level.setBlockAt(x, y, z, BlockID.POPPY);
                            }
                        } else {
                            level.setBlockAt(x, y, z, BlockID.TALL_GRASS, 1);
                        }
                    }

                    break;
                }

                x += random.nextRange(-1, 1);
                y += random.nextRange(-1, 1) * random.nextBoundedInt(3) / 2;
                z += random.nextRange(-1, 1);

                if (level.getBlockIdAt(x, y - 1, z) != BlockID.GRASS || y > 255 || y < 0) {
                    break;
                }

                ++num;
            }
        }
    }

}
