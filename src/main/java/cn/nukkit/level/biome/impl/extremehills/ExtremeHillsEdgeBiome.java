package cn.nukkit.level.biome.impl.extremehills;

/**
 * @author DaPorkchop_ (Nukkit Project)
 */
public class ExtremeHillsEdgeBiome extends ExtremeHillsBiome {

    public ExtremeHillsEdgeBiome() {
        super();

        this.setBaseHeight(0.8f);
        this.setHeightVariation(0.3f);
    }

    @Override
    public String getName() {
        return "Extreme Hills Edge";
    }
}
