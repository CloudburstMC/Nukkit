package cn.nukkit.level.biome.type;

import cn.nukkit.level.biome.Biome;

/**
 * author: DaPorkchop_
 * Nukkit Project
 * <p>
 * A biome with ground covering
 * </p>
 */
public abstract class CoveredBiome extends Biome {
    public final Object synchronizeCover = new Object();

    /**
     * A single block placed on top of the surface blocks
     *
     * @return cover block
     */
    public int getCoverBlock() {
        return AIR;
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

    /**
     * Between cover and ground
     *
     * @param y y
     * @return surface block
     */
    public abstract int getSurfaceBlock(int y);

    /**
     * The metadata of the surface block
     *
     * @param y y
     * @return surface meta
     */
    public int getSurfaceMeta(int y) {
        return 0;
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

    /**
     * Between surface and stone
     *
     * @param y y
     * @return ground block
     */
    public abstract int getGroundBlock(int y);

    /**
     * The metadata of the ground block
     *
     * @param y y
     * @return ground meta
     */
    public int getGroundMeta(int y) {
        return 0;
    }

    /**
     * The block used as stone/below all other surface blocks
     *
     * @return stone block
     */
    public int getStoneBlock() {
        return STONE;
    }

    /**
     * Called before a new block column is covered. Biomes can update any relevant variables here before covering.
     * <p>
     * Biome covering is synchronized on the biome, so thread safety isn't an issue.
     * </p>
     *
     * @param x x
     * @param z z
     */
    public void preCover(int x, int z) {

    }
}
