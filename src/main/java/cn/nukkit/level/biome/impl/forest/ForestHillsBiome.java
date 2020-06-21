package cn.nukkit.level.biome.impl.forest;

/**
 * author: DaPorkchop_
 * Nukkit Project
 */
public class ForestHillsBiome extends ForestBiome {

    public ForestHillsBiome() {
        this(ForestBiome.TYPE_NORMAL);
    }

    public ForestHillsBiome(final int type) {
        super(type);

        this.setBaseHeight(0.45f);
        this.setHeightVariation(0.3f);
    }

    @Override
    public String getName() {
        switch (this.type) {
            case ForestBiome.TYPE_BIRCH:
                return "Birch Forest Hills";
            case ForestBiome.TYPE_BIRCH_TALL:
                return "Birch Forest Hills M";
            default:
                return "Forest Hills";
        }
    }

}
