package cn.nukkit.item;

/**
 * @author Kaooot
 * @version 1.0
 */
public class ItemChestplateNetherite extends ItemArmor {

    public ItemChestplateNetherite() {
        this(0, 1);
    }

    public ItemChestplateNetherite(Integer meta) {
        this(meta, 1);
    }

    public ItemChestplateNetherite(Integer meta, int count) {
        super(NETHERITE_CHESTPLATE, meta, count, "Netherite Chestplate");
    }

    @Override
    public int getTier() {
        return ItemArmor.TIER_NETHERITE;
    }

    @Override
    public boolean isChestplate() {
        return true;
    }

    @Override
    public int getArmorPoints() {
        return 8;
    }

    @Override
    public int getMaxDurability() {
        return 593;
    }
}