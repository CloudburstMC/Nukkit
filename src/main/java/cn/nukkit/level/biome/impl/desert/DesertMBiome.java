package cn.nukkit.level.biome.impl.desert;

/**
 * author: DaPorkchop_
 * Nukkit Project
 */
public class DesertMBiome extends DesertBiome {
    public DesertMBiome() {
        super();

        this.setBaseHeight(0.225f);
        this.setHeightVariation(0.25f);
    }

    @Override
    public String getName() {
        return "Desert M";
    }
}
