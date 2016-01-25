package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class LeatherBoots extends Armor {

    public LeatherBoots() {
        this(0, 1);
    }

    public LeatherBoots(Integer meta) {
        this(meta, 1);
    }

    public LeatherBoots(Integer meta, int count) {
        super(LEATHER_BOOTS, meta, count, "Leather Boots");
    }

    @Override
    public int getTier() { return Armor.TIER_LEATHER; }

    @Override
    public boolean isBoots(){
        return true;
    }
}
