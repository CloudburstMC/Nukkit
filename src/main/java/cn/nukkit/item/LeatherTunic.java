package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class LeatherTunic extends Armor {

    public LeatherTunic() {
        this(0, 1);
    }

    public LeatherTunic(Integer meta) {
        this(meta, 1);
    }

    public LeatherTunic(Integer meta, int count) {
        super(LEATHER_TUNIC, meta, count, "Leather Tunic");
    }

    @Override
    public int getTier() { return Armor.TIER_LEATHER; }

    @Override
    public boolean isChestplate(){
        return true;
    }
}
