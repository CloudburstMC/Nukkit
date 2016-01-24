package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class PumpkinPie extends EdibleItem {

    public PumpkinPie() {
        this(0, 1);
    }

    public PumpkinPie(Integer meta) {
        this(meta, 1);
    }

    public PumpkinPie(Integer meta, int count) {
        super(PUMPKIN_PIE, meta, count, "Pumpkin Pie");
    }
}
