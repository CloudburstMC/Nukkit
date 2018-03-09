package cn.nukkit.level.biome.impl.desert;

/**
 * @author DaPorkchop_
 */
public class DesertHillsBiome extends DesertBiome {
    public DesertHillsBiome() {
        super();

        this.setElevation(70, 90);
    }

    @Override
    public String getName() {
        return "Desert Hills";
    }
}
