package cn.nukkit.level.biome.type;

import cn.nukkit.level.biome.Biome;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.level.generator.Normal;

/**
 * author: DaPorkchop_
 * Nukkit Project
 * <p>
 * A biome with ground covering
 * </p>
 */
public abstract class CoveredBiome extends Biome {
    public int getCoverId(int x, int z) {
        return AIR << 4;
    }

    public int getSurfaceDepth(int x, int y, int z) {
        return 1;
    }

    public abstract int getSurfaceId(int x, int y, int z);

    public int getGroundDepth(int x, int y, int z) {
        return 4;
    }

    public abstract int getGroundId(int x, int y, int z);

    public void doCover(int x, int z, Chunk chunk) {
        final int fullX = (chunk.getX() << 4) | x;
        final int fullZ = (chunk.getZ() << 4) | z;

        final int coverBlock = this.getCoverId(fullX, fullZ);

        boolean hasCovered = false;
        int realY;
        //start one below build limit in case of cover blocks
        for (int y = 254; y > 32; y--) {
            if (chunk.getFullBlock(x, y, z) == STONE << 4) {
                COVER:
                if (!hasCovered) {
                    if (y >= Normal.seaHeight) {
                        chunk.setFullBlock(x, y + 1, z, coverBlock);
                        int surfaceDepth = this.getSurfaceDepth(fullX, y, fullZ);
                        for (int i = 0; i < surfaceDepth; i++) {
                            realY = y - i;
                            if (chunk.getFullBlock(x, realY, z) == STONE << 4) {
                                chunk.setFullBlock(x, realY, z, this.getSurfaceId(fullX, realY, fullZ));
                            } else break COVER;
                        }
                        y -= surfaceDepth;
                    }
                    int groundDepth = this.getGroundDepth(fullX, y, fullZ);
                    for (int i = 0; i < groundDepth; i++) {
                        realY = y - i;
                        if (chunk.getFullBlock(x, realY, z) == STONE << 4) {
                            chunk.setFullBlock(x, realY, z, this.getGroundId(fullX, realY, fullZ));
                        } else break COVER;
                    }
                    //don't take all of groundDepth away because we do y-- in the loop
                    y -= groundDepth - 1;
                }
                hasCovered = true;
            } else {
                if (hasCovered) {
                    //reset it if this isn't a valid stone block (allows us to place ground cover on top and below overhangs)
                    hasCovered = false;
                }
            }
        }
    }
}
