package cn.nukkit.level.generator.object;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;

public class ObjectNyliumVegetation {
    public static void growVegetation(ChunkManager level, Vector3 pos, NukkitRandom random) {
        for (int i = 0; i < 128; ++i) {
            int num = 0;

            int x = pos.getFloorX();
            int y = pos.getFloorY() + 1;
            int z = pos.getFloorZ();
            
            boolean crimson = level.getBlockIdAt(x, y-1, z) == BlockID.CRIMSON_NYLIUM;

            while (true) {
                if (num >= i / 16) {
                    if (level.getBlockIdAt(x, y, z) == BlockID.AIR) {
                        if (crimson) {
                            if (random.nextBoundedInt(8) == 0) {
                                if (random.nextBoundedInt(8) == 0) {
                                    level.setBlockAt(x, y, z, BlockID.WARPED_FUNGUS);
                                } else {
                                    level.setBlockAt(x, y, z, BlockID.CRIMSON_FUNGUS);
                                }
                            } else {
                                level.setBlockAt(x, y, z, BlockID.CRIMSON_ROOTS);
                            }
                        } else {
                            if (random.nextBoundedInt(8) == 0) {
                                if (random.nextBoundedInt(8) == 0) {
                                    level.setBlockAt(x, y, z, BlockID.CRIMSON_FUNGUS);
                                } else {
                                    level.setBlockAt(x, y, z, BlockID.WARPED_FUNGUS);
                                }
                            } else {
                                if (random.nextBoolean()) {
                                    level.setBlockAt(x, y, z, BlockID.WARPED_ROOTS);
                                } else {
                                    level.setBlockIdAt(x, y, z, BlockID.NETHER_SPROUTS_BLOCK);
                                }
                            }
                        }
                    }

                    break;
                }

                x += random.nextRange(-1, 1);
                y += random.nextRange(-1, 1) * random.nextBoundedInt(3) / 2;
                z += random.nextRange(-1, 1);

                int id = level.getBlockIdAt(x, y - 1, z);
                crimson = id == BlockID.CRIMSON_NYLIUM;
                if ((!crimson && id != BlockID.WARPED_NYLIUM) || y > 255 || y < 0) {
                    break;
                }

                ++num;
            }
        }
    }
}
