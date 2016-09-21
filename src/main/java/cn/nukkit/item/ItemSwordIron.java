package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemSwordIron extends ItemTool {

    public ItemSwordIron() {
        this(0, 1);
    }

    public ItemSwordIron(Integer meta) {
        this(meta, 1);
    }

    public ItemSwordIron(Integer meta, int count) {
        super(IRON_SWORD, meta, count, "Iron Sword");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_IRON;
    }

    @Override
    public boolean isSword() {
        return true;
    }

    @Override
    public int getTier() {
        return ItemTool.TIER_IRON;
    }

    @Override
    public int getAttackDamage() {
        return 6;
    }
}
