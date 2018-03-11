package cn.nukkit.level.biome.impl.mesa;

/**
 * @author DaPorkchop_
 */
//porktodo: the plateaus here are smaller and less frequent than in the normal counterpart (which is one giant plateau)
public class MesaPlateauMBiome extends MesaBiome {
    public MesaPlateauMBiome() {
        super();

        this.setMoundHeight(10);
    }

    @Override
    public String getName() {
        return "Mesa Plateau M";
    }



    @Override
    protected float getMoundFrequency() {
        return 1 / 50f;
    }

    @Override
    protected float minHill() {
        return 0.1f;
    }
}
