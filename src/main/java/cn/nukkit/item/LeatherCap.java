package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class LeatherCap extends Armor {

    public LeatherCap() {
        this(0, 1);
    }

    public LeatherCap(Integer meta) {
        this(meta, 1);
    }

    public LeatherCap(Integer meta, int count) {
        super(LEATHER_CAP, meta, count, "Leather Cap");
    }

    @Override
    public int getTier() {
        return Armor.TIER_LEATHER;
    }

    @Override
    public boolean isHelmet() {
        return true;
    }
}
