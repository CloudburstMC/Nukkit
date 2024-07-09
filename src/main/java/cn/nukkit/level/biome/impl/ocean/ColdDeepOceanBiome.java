package cn.nukkit.level.biome.impl.ocean;

public class ColdDeepOceanBiome extends ColdOceanBiome {

    public ColdDeepOceanBiome() {
        super();

        this.setBaseHeight(-1.8f);
        this.setHeightVariation(0.1f);
    }

    @Override
    public String getName() {
        return "Deep Cold Ocean";
    }
}