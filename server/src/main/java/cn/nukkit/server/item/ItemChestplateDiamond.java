package cn.nukkit.server.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemChestplateDiamond extends ItemArmor {

    public ItemChestplateDiamond() {
        this(0, 1);
    }

    public ItemChestplateDiamond(Integer meta) {
        this(meta, 1);
    }

    public ItemChestplateDiamond(Integer meta, int count) {
        super(DIAMOND_CHESTPLATE, meta, count, "Diamond Chestplate");
    }

    @Override
    public int getTier() {
        return ItemArmor.TIER_DIAMOND;
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
        return 529;
    }

    @Override
    public int getToughness() {
        return 2;
    }
}
