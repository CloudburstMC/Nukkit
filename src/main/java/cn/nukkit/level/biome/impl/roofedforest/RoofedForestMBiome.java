package cn.nukkit.level.biome.impl.roofedforest;

public class RoofedForestMBiome extends RoofedForestBiome {

    public RoofedForestMBiome() {
        super();

        this.setBaseHeight(0.2f);
        this.setHeightVariation(0.4f);
    }

    @Override
    public String getName() {
        return "Roofed Forest M";
    }
}
