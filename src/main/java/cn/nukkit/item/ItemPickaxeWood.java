package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemPickaxeWood extends ItemTool {

    public ItemPickaxeWood() {
        this(0, 1);
    }

    public ItemPickaxeWood(Integer meta) {
        this(meta, 1);
    }

    public ItemPickaxeWood(Integer meta, int count) {
        super(WOODEN_PICKAXE, meta, count, "Wooden Pickaxe");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_WOODEN;
    }

    @Override
    public boolean isPickaxe() {
        return true;
    }

    @Override
    public int getTier() {
        return ItemTool.TIER_WOODEN;
    }
}
