package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemLeggingsIron extends ItemArmor {

    public ItemLeggingsIron() {
        this(0, 1);
    }

    public ItemLeggingsIron(Integer meta) {
        this(meta, 1);
    }

    public ItemLeggingsIron(Integer meta, int count) {
        super(IRON_LEGGINGS, meta, count, "Iron Leggings");
    }

    @Override
    public int getTier() {
        return ItemArmor.TIER_IRON;
    }

    @Override
    public boolean isLeggings() {
        return true;
    }
}
