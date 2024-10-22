package cn.nukkit.level.biome.impl.ocean;

public class NewFrozenOceanBiome extends OceanBiome {

    public NewFrozenOceanBiome() {
        super();
    }

    @Override
    public String getName() {
        return "Frozen Ocean";
    }

    @Override
    public boolean isFreezing() {
        return true;
    }

    @Override
    public boolean canRain() {
        return false;
    }
}
