package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemAxeGold extends ItemTool {

    public ItemAxeGold() {
        this(0, 1);
    }

    public ItemAxeGold(Integer meta) {
        this(meta, 1);
    }

    public ItemAxeGold(Integer meta, int count) {
        super(GOLD_AXE, meta, count, "Gold Axe");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_GOLD;
    }

    @Override
    public boolean isAxe() {
        return true;
    }

    @Override
    public int getTier() {
        return ItemTool.TIER_GOLD;
    }
}
