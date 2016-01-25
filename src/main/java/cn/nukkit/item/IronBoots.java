package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class IronBoots extends Armor {

    public IronBoots() {
        this(0, 1);
    }

    public IronBoots(Integer meta) {
        this(meta, 1);
    }

    public IronBoots(Integer meta, int count) {
        super(IRON_BOOTS, meta, count, "Iron Boots");
    }

    @Override
    public int getTier() { return Armor.TIER_IRON; }

    @Override
    public boolean isBoots(){
        return true;
    }
}
