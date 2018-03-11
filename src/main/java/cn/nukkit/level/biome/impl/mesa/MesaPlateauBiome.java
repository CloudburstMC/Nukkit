package cn.nukkit.level.biome.impl.mesa;

/**
 * @author DaPorkchop_
 */
public class MesaPlateauBiome extends MesaBiome {
    public MesaPlateauBiome() {
        super();

        this.setElevation(95, 100);

        this.setMoundHeight(0);
    }

    @Override
    public String getName() {
        return "Mesa Plateau";
    }
}
