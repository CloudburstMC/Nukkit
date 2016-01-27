package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class LeatherPants extends Armor {

    public LeatherPants() {
        this(0, 1);
    }

    public LeatherPants(Integer meta) {
        this(meta, 1);
    }

    public LeatherPants(Integer meta, int count) {
        super(LEATHER_PANTS, meta, count, "Leather Pants");
    }

    @Override
    public int getTier() {
        return Armor.TIER_LEATHER;
    }

    @Override
    public boolean isLeggings() {
        return true;
    }
}
