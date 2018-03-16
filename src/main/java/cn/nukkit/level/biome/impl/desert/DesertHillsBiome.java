package cn.nukkit.level.biome.impl.desert;

/**
 * @author DaPorkchop_
 */
public class DesertHillsBiome extends DesertBiome {
    public DesertHillsBiome() {
        super();

        this.setBaseHeight(0.45f);
        this.setHeightVariation(0.3f);
    }

    @Override
    public String getName() {
        return "Desert Hills";
    }
}
