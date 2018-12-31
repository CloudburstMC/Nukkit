package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemChestplateIron extends ItemArmor {

    public ItemChestplateIron() {
        this(0, 1);
    }

    public ItemChestplateIron(Integer meta) {
        this(meta, 1);
    }

    public ItemChestplateIron(Integer meta, int count) {
        super(IRON_CHESTPLATE, meta, count, "Iron Chestplate");
    }

    @Override
    public int getTier() {
        return ItemArmor.TIER_IRON;
    }

    @Override
    public boolean isChestplate() {
        return true;
    }

    @Override
    public int getMaxDurability() {
        return 241;
    }
}
