package cn.nukkit.level.biome.impl.ocean;

/**
 * author: DaPorkchop_
 * Nukkit Project
 */
public class DeepOceanBiome extends OceanBiome {

    public DeepOceanBiome() {
        super();

        //TODO: ocean monuments
        this.setElevation(30, 46);
    }

    @Override
    public String getName() {
        return "Deep Ocean";
    }
}
