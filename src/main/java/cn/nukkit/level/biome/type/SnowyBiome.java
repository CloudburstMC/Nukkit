package cn.nukkit.level.biome.type;

/**
 * author: DaPorkchop_
 * Nukkit Project
 */
public abstract class SnowyBiome extends GrassyBiome {
    public SnowyBiome() {
        super();
    }

    @Override
    public int getCoverBlock() {
        return SNOW_LAYER;
    }
}
