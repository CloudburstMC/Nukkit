package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemAxeWood extends ItemTool {

    public ItemAxeWood() {
        this(0, 1);
    }

    public ItemAxeWood(Integer meta) {
        this(meta, 1);
    }

    public ItemAxeWood(Integer meta, int count) {
        super(WOODEN_AXE, meta, count, "Wooden Axe");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_WOODEN;
    }

    @Override
    public boolean isAxe() {
        return true;
    }

    @Override
    public int getTier() {
        return ItemTool.TIER_WOODEN;
    }
}
