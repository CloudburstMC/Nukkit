package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class LeatherTunic extends Armor {

    public LeatherTunic() {
        this(0, 1);
    }

    public LeatherTunic(int meta) {
        this(meta, 1);
    }

    public LeatherTunic(int meta, int count) {
        super(LEATHER_TUNIC, meta, count, "Leather Tunic");
    }
}
