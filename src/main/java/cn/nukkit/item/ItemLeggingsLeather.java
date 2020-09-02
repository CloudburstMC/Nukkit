package cn.nukkit.item;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemLeggingsLeather extends ItemColorArmor {

    public ItemLeggingsLeather() {
        this(0, 1);
    }

    public ItemLeggingsLeather(Integer meta) {
        this(meta, 1);
    }

    public ItemLeggingsLeather(Integer meta, int count) {
        super(LEATHER_PANTS, meta, count, "Leather Pants");
    }

    @Override
    public int getTier() {
        return ItemArmor.TIER_LEATHER;
    }

    @Override
    public boolean isLeggings() {
        return true;
    }

    @Override
    public int getArmorPoints() {
        return 2;
    }

    @Override
    public int getMaxDurability() {
        return 76;
    }
}
