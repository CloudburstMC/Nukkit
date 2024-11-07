package cn.nukkit.level.biome.impl.savanna;

/**
 * @author DaPorkchop_
 */
public class SavannaPlateauMBiome extends SavannaPlateauBiome {

    public SavannaPlateauMBiome() {
        super();

        this.setBaseHeight(1.05f);
        this.setHeightVariation(1.2125001f);
    }

    @Override
    public String getName() {
        return "Savanna Plateau M";
    }

    @Override
    public boolean doesOverhang() {
        return true;
    }
}
