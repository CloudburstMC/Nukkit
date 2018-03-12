package cn.nukkit.level.biome.impl.savanna;

/**
 * @author DaPorkchop_
 */
//porktodo: this is just like savanna plateau with individual spikes
//see https://minecraft.gamepedia.com/Biome#Plateau_M
public class SavannaPlateauMBiome extends SavannaPlateauBiome {

    public SavannaPlateauMBiome() {
        super();

        this.setBaseHeight(1.05f);
        this.setHeightVariation(1.2125001f);
    }

    @Override
    public String getName() {
        return "Savanna Plateau M";
    }

    @Override
    public boolean doesOverhang() {
        return true;
    }
}
