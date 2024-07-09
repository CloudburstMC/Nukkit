package cn.nukkit.level.biome.impl.ocean;

public class LukewarmDeepOceanBiome extends LukewarmOceanBiome {

    public LukewarmDeepOceanBiome() {
        super();

        this.setBaseHeight(-1.8f);
        this.setHeightVariation(0.1f);
    }

    @Override
    public String getName() {
        return "Deep Lukewarm Ocean";
    }
}
