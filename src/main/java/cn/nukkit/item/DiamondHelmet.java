package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class DiamondHelmet extends Armor {

    public DiamondHelmet() {
        this(0, 1);
    }

    public DiamondHelmet(int meta) {
        this(meta, 1);
    }

    public DiamondHelmet(int meta, int count) {
        super(DIAMOND_HELMET, meta, count, "Diamond Helmet");
    }
}
