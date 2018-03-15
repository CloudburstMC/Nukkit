package cn.nukkit.level.biome.impl.savanna;

/**
 * @author DaPorkchop_
 */
public class SavannaPlateauBiome extends SavannaBiome {

    public SavannaPlateauBiome() {
        super();

        this.setBaseHeight(1.5f);
        this.setHeightVariation(0.025f);
    }

    @Override
    public String getName() {
        return "Savanna Plateau";
    }
}
