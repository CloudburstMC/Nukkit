package cn.nukkit.level.biome.impl.mesa;

/**
 * @author DaPorkchop_
 */
public class MesaBryceBiome extends MesaBiome {

    @Override
    public String getName() {
        return "Mesa (Bryce)";
    }

    @Override
    protected float getMoundFrequency() {
        return 0.0625f;
    }

    @Override
    protected float minHill() {
        return 0.3f;
    }
}
