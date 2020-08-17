package cn.nukkit.item;

/**
 * @author Kaooot
 * @version 1.0
 */
public class ItemLeggingsNetherite extends ItemArmor {

    public ItemLeggingsNetherite() {
        this(0, 1);
    }

    public ItemLeggingsNetherite(Integer meta) {
        this(meta, 1);
    }

    public ItemLeggingsNetherite(Integer meta, int count) {
        super(NETHERITE_LEGGINGS, meta, count, "Netherite Leggings");
    }

    @Override
    public int getTier() {
        return ItemArmor.TIER_NETHERITE;
    }

    @Override
    public boolean isLeggings() {
        return true;
    }

    @Override
    public int getArmorPoints() {
        return 6;
    }

    @Override
    public int getMaxDurability() {
        return 556;
    }
}