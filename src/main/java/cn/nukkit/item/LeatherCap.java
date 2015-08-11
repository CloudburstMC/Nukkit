package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class LeatherCap extends Armor {

    public LeatherCap() {
        this(0, 1);
    }

    public LeatherCap(int meta) {
        this(meta, 1);
    }

    public LeatherCap(int meta, int count) {
        super(LEATHER_CAP, meta, count, "Leather Cap");
    }
}
