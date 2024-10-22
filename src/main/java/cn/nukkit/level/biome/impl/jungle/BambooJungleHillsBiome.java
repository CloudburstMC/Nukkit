package cn.nukkit.level.biome.impl.jungle;

public class BambooJungleHillsBiome extends BambooJungleBiome {

    public BambooJungleHillsBiome() {
        super();

        this.setBaseHeight(0.45f);
        this.setHeightVariation(0.3f);
    }

    @Override
    public String getName() {
        return "Bamboo Jungle Hills";
    }
}