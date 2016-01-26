package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class DiamondHelmet extends Armor {

    public DiamondHelmet() {
        this(0, 1);
    }

    public DiamondHelmet(Integer meta) {
        this(meta, 1);
    }

    public DiamondHelmet(Integer meta, int count) {
        super(DIAMOND_HELMET, meta, count, "Diamond Helmet");
    }

    @Override
    public int getTier() {
        return Armor.TIER_DIAMOND;
    }

    @Override
    public boolean isHelmet() {
        return true;
    }
}
