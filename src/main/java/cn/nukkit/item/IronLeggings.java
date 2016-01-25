package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class IronLeggings extends Armor {

    public IronLeggings() {
        this(0, 1);
    }

    public IronLeggings(Integer meta) {
        this(meta, 1);
    }

    public IronLeggings(Integer meta, int count) {
        super(IRON_LEGGINGS, meta, count, "Iron Leggings");
    }

    @Override
    public int getTier() { return Armor.TIER_IRON; }

    @Override
    public boolean isLeggings(){
        return true;
    }
}
