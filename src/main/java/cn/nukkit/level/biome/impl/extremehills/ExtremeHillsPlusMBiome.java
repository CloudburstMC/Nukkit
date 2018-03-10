package cn.nukkit.level.biome.impl.extremehills;

/**
 * author: DaPorkchop_
 * Nukkit Project
 * <p>
 * make sure this is touching another extreme hills type or it'll look dumb
 *
 * very steep (1-2 block at a time) hills with round tops. flat in between
 */
//porktodo: this has no overhangs!
public class ExtremeHillsPlusMBiome extends ExtremeHillsMBiome {

    public ExtremeHillsPlusMBiome() {
        super(false);

        this.setElevation(70, 140);
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
