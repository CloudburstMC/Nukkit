package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemShovelIron extends ItemTool {

    public ItemShovelIron() {
        this(0, 1);
    }

    public ItemShovelIron(Integer meta) {
        this(meta, 1);
    }

    public ItemShovelIron(Integer meta, int count) {
        super(IRON_SHOVEL, meta, count, "Iron Shovel");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_IRON;
    }

    @Override
    public boolean isShovel() {
        return true;
    }

    @Override
    public int getTier() {
        return ItemTool.TIER_IRON;
    }
}
