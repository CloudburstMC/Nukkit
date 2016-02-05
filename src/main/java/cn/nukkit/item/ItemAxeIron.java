package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemAxeIron extends ItemTool {

    public ItemAxeIron() {
        this(0, 1);
    }

    public ItemAxeIron(Integer meta) {
        this(meta, 1);
    }

    public ItemAxeIron(Integer meta, int count) {
        super(IRON_AXE, meta, count, "Iron Axe");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_IRON;
    }

    @Override
    public boolean isAxe() {
        return true;
    }

    @Override
    public int getTier() {
        return ItemTool.TIER_IRON;
    }
}
