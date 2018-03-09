package cn.nukkit.level.biome.impl.forest;

/**
 * author: DaPorkchop_
 * Nukkit Project
 */
public class ForestHillsBiome extends ForestBiome {
    public ForestHillsBiome() {
        this(TYPE_NORMAL);
    }

    public ForestHillsBiome(int type) {
        super(type);

        this.setElevation(70, 90);
    }

    @Override
    public String getName() {
        switch (this.type) {
            case TYPE_BIRCH:
                return "Birch Forest Hills";
            case TYPE_BIRCH_TALL:
                return "Birch Forest Hills M";
            default:
                return "Forest Hills";
        }
    }
}
