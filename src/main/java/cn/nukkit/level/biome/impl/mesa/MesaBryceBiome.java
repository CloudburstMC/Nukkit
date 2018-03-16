package cn.nukkit.level.biome.impl.mesa;

/**
 * @author DaPorkchop_
 */
public class MesaBryceBiome extends MesaBiome {
    public MesaBryceBiome() {
        super();
    }

    @Override
    public String getName() {
        return "Mesa (Bryce)";
    }

    @Override
    protected float getMoundFrequency() {
        return 1 / 16f;
    }

    @Override
    protected float minHill() {
        return 0.3f;
    }
}
