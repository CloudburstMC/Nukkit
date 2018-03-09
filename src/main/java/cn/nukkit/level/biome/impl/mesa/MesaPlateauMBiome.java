package cn.nukkit.level.biome.impl.mesa;

/**
 * @author DaPorkchop_
 */
//porktodo: the plateaus here are smaller and less frequent than in the normal counterpart (which is one giant plateau)
public class MesaPlateauMBiome extends MesaPlateauBiome {
    public MesaPlateauMBiome()  {
        super();

        this.setElevation(80, 84);
    }

    @Override
    public String getName() {
        return "Mesa Plateau M";
    }
}
