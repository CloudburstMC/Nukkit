package cn.nukkit.level.biome.impl.mushroom;

/**
 * @author DaPorkchop_
 */
public class MushroomIslandShoreBiome extends MushroomIslandBiome {
    public MushroomIslandShoreBiome() {
        super();

        this.setBaseHeight(0f);
        this.setHeightVariation(0.025f);
    }

    @Override
    public String getName() {
        return "Mushroom Island Shore";
    }
}
