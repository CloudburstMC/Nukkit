package cn.nukkit.level.biome.type;

import cn.nukkit.level.biome.Biome;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.Normal;

/**
 * author: DaPorkchop_
 * Nukkit Project
 * <p>
 * A biome with ground covering
 * </p>
 */
public abstract class CoveredBiome extends Biome {
    /**
     * A single block placed on top of the surface blocks
     *
     * @return cover block
     */
    public int getCoverBlock() {
        return AIR;
    }

    /**
     * The metadata of the cover block
     *
     * @return cover block
     */
    public int getCoverMeta() {
        return 0;
    }

    /**
     * The amount of times the surface block should be used
     * <p>
     * If &lt; 0 bad things will happen!
     * </p>
     *
     * @param y y
     * @return surface depth
     */
    public int getSurfaceDepth(int y) {
        return 1;
    }

    public int getSurfaceDepth(int x, int y, int z) {
        return this.getSurfaceDepth(y);
    }

    /**
     * Between cover and ground
     *
     * @param y y
     * @return surface block
     */
    public abstract int getSurfaceBlock(int y);

    public int getSurfaceBlock(int x, int y, int z) {
        return this.getSurfaceBlock(y);
    }

    /**
     * The metadata of the surface block
     *
     * @param y y
     * @return surface meta
     */
    public int getSurfaceMeta(int y) {
        return 0;
    }

    public int getSurfaceMeta(int x, int y, int z) {
        return this.getSurfaceMeta(y);
    }

    /**
     * The amount of times the ground block should be used
     * <p>
     * If &lt; 0 bad things will happen!
     *
     * @param y y
     * @return ground depth
     */
    public int getGroundDepth(int y) {
        return 4;
    }

    public int getGroundDepth(int x, int y, int z) {
        return this.getGroundDepth(y);
    }

    /**
     * Between surface and stone
     *
     * @param y y
     * @return ground block
     */
    public abstract int getGroundBlock(int y);

    public int getGroundBlock(int x, int y, int z) {
        return this.getGroundBlock(y);
    }

    /**
     * The metadata of the ground block
     *
     * @param y y
     * @return ground meta
     */
    public int getGroundMeta(int y) {
        return 0;
    }

    public int getGroundMeta(int x, int y, int z) {
        return this.getGroundMeta(y);
    }

    /**
     * The block used as stone/below all other surface blocks
     *
     * @return stone block
     */
    public int getStoneBlock() {
        return STONE;
    }

    public void doCover(int x, int z, FullChunk chunk) {
        final int coverBlock = (this.getCoverBlock() << 4) | this.getCoverMeta();

        final int fullX = (chunk.getX() << 4) | x;
        final int fullZ = (chunk.getZ() << 4) | z;

        boolean hasCovered = false;
        int realY;
        //start one below build limit in case of cover blocks
        for (int y = 254; y > 32; y--) {
            if (chunk.getFullBlock(x, y, z) == STONE) {
                COVER:
                if (!hasCovered) {
                    if (y >= Normal.seaHeight) {
                        chunk.setFullBlockId(x, y + 1, z, coverBlock);
                        int surfaceDepth = this.getSurfaceDepth(fullX, y, fullZ);
                        for (int i = 0; i < surfaceDepth; i++) {
                            realY = y - i;
                            if (chunk.getFullBlock(x, realY, z) == STONE) {
                                chunk.setFullBlockId(x, realY, z, (this.getSurfaceBlock(fullX, realY, fullZ) << 4) | this.getSurfaceMeta(fullX, realY, fullZ));
                            } else break COVER;
                        }
                        y -= surfaceDepth;
                    }
                    int groundDepth = this.getGroundDepth(fullX, y, fullZ);
                    for (int i = 0; i < groundDepth; i++) {
                        realY = y - i;
                        if (chunk.getFullBlock(x, realY, z) == STONE) {
                            chunk.setFullBlockId(x, realY, z, (this.getGroundBlock(fullX, realY, fullZ) << 4) | this.getGroundMeta(fullX, realY, fullZ));
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
