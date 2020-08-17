package cn.nukkit.item;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemBootsIron extends ItemArmor {

    public ItemBootsIron() {
        this(0, 1);
    }

    public ItemBootsIron(Integer meta) {
        this(meta, 1);
    }

    public ItemBootsIron(Integer meta, int count) {
        super(IRON_BOOTS, meta, count, "Iron Boots");
    }

    @Override
    public int getTier() {
        return ItemArmor.TIER_IRON;
    }

    @Override
    public boolean isBoots() {
        return true;
    }

    @Override
    public int getArmorPoints() {
        return 2;
    }

    @Override
    public int getMaxDurability() {
        return 196;
    }
}
