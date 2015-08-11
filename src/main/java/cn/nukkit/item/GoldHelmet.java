package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class GoldHelmet extends Armor {

    public GoldHelmet() {
        this(0, 1);
    }

    public GoldHelmet(int meta) {
        this(meta, 1);
    }

    public GoldHelmet(int meta, int count) {
        super(GOLD_HELMET, meta, count, "Gold Helmet");
    }
}
