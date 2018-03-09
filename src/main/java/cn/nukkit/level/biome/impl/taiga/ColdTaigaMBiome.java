package cn.nukkit.level.biome.impl.taiga;

/**
 * author: DaPorkchop_
 * Nukkit Project
 */
//porktodo: this biome has very steep cliffs
public class ColdTaigaMBiome extends ColdTaigaBiome {
    public ColdTaigaMBiome() {
        super();

        this.setElevation(70, 90);
    }

    @Override
    public String getName() {
        return "Cold Taiga M";
    }
}
