package cn.nukkit.level.biome.impl.taiga;

/**
 * @author DaPorkchop_
 * Nukkit Project
 */
public class TaigaHillsBiome extends TaigaBiome {

    public TaigaHillsBiome() {
        super();

        this.setElevation(70, 90);
    }

    @Override
    public String getName() {
        return "Taiga Hills";
    }
}
