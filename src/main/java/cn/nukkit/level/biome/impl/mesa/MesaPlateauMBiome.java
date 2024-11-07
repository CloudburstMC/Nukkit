package cn.nukkit.level.biome.impl.mesa;

/**
 * @author DaPorkchop_
 */
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
        return 0.02f;
    }

    @Override
    protected float minHill() {
        return 0.1f;
    }
}
