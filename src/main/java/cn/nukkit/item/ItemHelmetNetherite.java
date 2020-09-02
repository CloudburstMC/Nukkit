package cn.nukkit.item;

public class ItemHelmetNetherite extends ItemArmor {
    public ItemHelmetNetherite() {
        this(0, 1);
    }

    public ItemHelmetNetherite(Integer meta) {
        this(meta, 1);
    }

    public ItemHelmetNetherite(Integer meta, int count) {
        super(NETHERITE_HELMET, meta, count, "Netherite Helmet");
    }

    @Override
    public int getTier() {
        return ItemArmor.TIER_NETHERITE;
    }

    @Override
    public boolean isHelmet() {
        return true;
    }

    @Override
    public int getArmorPoints() {
        return 3;
    }

    @Override
    public int getMaxDurability() {
        return 407;
    }

    @Override
    public int getToughness() {
        return 3;
    }

    @Override
    public boolean isLavaResistant() {
        return true;
    }
}
