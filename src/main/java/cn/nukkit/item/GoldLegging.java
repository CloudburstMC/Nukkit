package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class GoldLegging extends Armor {

    public GoldLegging() {
        this(0, 1);
    }

    public GoldLegging(int meta) {
        this(meta, 1);
    }

    public GoldLegging(int meta, int count) {
        super(GOLD_LEGGINGS, meta, count, "Gold Leggings");
    }
}
