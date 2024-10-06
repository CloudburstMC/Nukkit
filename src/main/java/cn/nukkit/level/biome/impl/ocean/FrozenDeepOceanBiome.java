package cn.nukkit.level.biome.impl.ocean;

public class FrozenDeepOceanBiome extends NewFrozenOceanBiome {

    public FrozenDeepOceanBiome() {
        super();

        this.setBaseHeight(-1.8f);
        this.setHeightVariation(0.1f);
    }

    @Override
    public String getName() {
        return "Deep Frozen Ocean";
    }
}
