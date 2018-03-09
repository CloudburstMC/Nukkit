package cn.nukkit.level.biome.impl.swamp;

/**
 * author: DaPorkchop_
 * Nukkit Project
 */
//porktodo: this should be flat in most places, and only rise up in a few
public class SwamplandMBiome extends SwampBiome {

    public SwamplandMBiome() {
        super();

        this.setElevation(62, 75);
    }

    @Override
    public String getName() {
        return "Swampland M";
    }
}
