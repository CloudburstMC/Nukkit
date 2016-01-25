package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class IronChestplate extends Armor {

    public IronChestplate() {
        this(0, 1);
    }

    public IronChestplate(Integer meta) {
        this(meta, 1);
    }

    public IronChestplate(Integer meta, int count) {
        super(IRON_CHESTPLATE, meta, count, "Iron Chestplate");
    }

    @Override
    public int getTier() { return Armor.TIER_IRON; }

    @Override
    public boolean isChestplate(){
        return true;
    }
}
