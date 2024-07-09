package cn.nukkit.level.biome.impl.ocean;

public class WarmDeepOceanBiome extends WarmOceanBiome {

    public WarmDeepOceanBiome() {
        super();

        this.setBaseHeight(-1.8f);
        this.setHeightVariation(0.1f);
    }

    @Override
    public String getName() {
        return "Deep Warm Ocean";
    }
}
