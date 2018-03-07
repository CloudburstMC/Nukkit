package cn.nukkit.level.generator.biome.type;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.generator.biome.Biome;

/**
 * author: DaPorkchop_
 * Nukkit Project
 *
 * A biome with ground covering
 */
public abstract class CoveredBiome extends Biome implements BlockID {
    @Override
    public int getColor() {
        return this.grassColor;
    }

    /**
     * A single block placed on top of the surface blocks
     */
    public int getCoverBlock()  {
        return AIR;
    }

    /**
     * The amount of times the surface block should be used
     *
     * If < 0 bad things will happen!
     */
    public int getSurfaceDepth()    {
        return 1;
    }

    /**
     * Between cover and ground
     */
    public abstract int getSurfaceBlock();

    /**
     * The metadata of the surface block
     */
    public int getSurfaceMeta() {
        return 0;
    }

    /**
     * The amount of times the ground block should be used
     *
     * If < 0 bad things will happen!
     */
    public int getGroundDepth() {
        return 4;
    }

    /**
     * Between surface and stone
     */
    public abstract int getGroundBlock();

    /**
     * The metadata of the ground block
     */
    public int getGroundMeta()  {
        return 0;
    }

    /**
     * The block used as stone/below all other surface blocks
     */
    public int getStoneBlock() {
        return STONE;
    }
}
