package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemAxeDiamond extends ItemTool {

    public ItemAxeDiamond() {
        this(0, 1);
    }

    public ItemAxeDiamond(Integer meta) {
        this(meta, 1);
    }

    public ItemAxeDiamond(Integer meta, int count) {
        super(DIAMOND_AXE, meta, count, "Diamond Axe");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_DIAMOND;
    }

    @Override
    public boolean isAxe() {
        return true;
    }

    @Override
    public int getTier() {
        return ItemTool.TIER_DIAMOND;
    }

    @Override
    public int getAttackDamage() {
        return 6;
    }
}
