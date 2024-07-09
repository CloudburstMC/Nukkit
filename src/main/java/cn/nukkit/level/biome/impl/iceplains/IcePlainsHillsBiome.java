package cn.nukkit.level.biome.impl.iceplains;

public class IcePlainsHillsBiome extends IcePlainsBiome {

    public IcePlainsHillsBiome() {
        super();

        this.setBaseHeight(0.45f);
        this.setHeightVariation(0.3f);
    }

    public String getName() {
        return "Snowy Mountains";
    }
}