package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemSwordGold extends ItemTool {

    public ItemSwordGold() {
        this(0, 1);
    }

    public ItemSwordGold(Integer meta) {
        this(meta, 1);
    }

    public ItemSwordGold(Integer meta, int count) {
        super(GOLD_SWORD, meta, count, "Golden Sword");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_GOLD;
    }

    @Override
    public boolean isSword() {
        return true;
    }

    @Override
    public int getTier() {
        return ItemTool.TIER_GOLD;
    }

    @Override
    public int getAttackDamage() {
        return 4;
    }
}
