package cn.nukkit.item;

/**
 * @author Kaooot
 * @version 1.0
 */
public class ItemBootsNetherite extends ItemArmor {

    public ItemBootsNetherite() {
        this(0, 1);
    }

    public ItemBootsNetherite(Integer meta) {
        this(meta, 1);
    }

    public ItemBootsNetherite(Integer meta, int count) {
        super(NETHERITE_BOOTS, meta, count, "Netherite Boots");
    }

    @Override
    public int getTier() {
        return ItemArmor.TIER_NETHERITE;
    }

    @Override
    public boolean isBoots() {
        return true;
    }

    @Override
    public int getArmorPoints() {
        return 3;
    }

    @Override
    public int getMaxDurability() {
        return 482;
    }
}