package cn.nukkit.level.biome.impl.extremehills;

/**
 * author: DaPorkchop_
 * Nukkit Project
 * <p>
 * make sure this is touching another extreme hills type or it'll look dumb
 *
 * very steep (1-2 block at a time) hills with round tops. flat in between
 */
public class ExtremeHillsPlusMBiome extends ExtremeHillsMBiome {

    public ExtremeHillsPlusMBiome() {
        super(false);

        this.setBaseHeight(1f);
        this.setHeightVariation(0.5f);
    }

    @Override
    public String getName() {
        return "Extreme Hills+ M";
    }

    @Override
    public boolean doesOverhang() {
        return false;
    }
}
