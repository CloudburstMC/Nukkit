package cn.nukkit.item;

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
    public boolean isChestplate() {
        return true;
    }

    @Override
    public int getTier() {
        return ItemArmor.TIER_NETHERITE;
    }

    @Override
    public int getMaxDurability() {
        return 592;
    }

    @Override
    public int getArmorPoints() {
        return 8;
    }

    @Override
    public int getToughness() {
        return 2;
    }
}
