package cn.nukkit.level.biome.impl.taiga;

/**
 * author: DaPorkchop_
 * Nukkit Project
 */
public class ColdTaigaMBiome extends ColdTaigaBiome {
    public ColdTaigaMBiome() {
        super();
    }

    @Override
    public String getName() {
        return "Cold Taiga M";
    }

    @Override
    public boolean doesOverhang() {
        return true;
    }
}
