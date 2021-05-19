package cn.nukkit.item;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemHoeWood extends ItemTool {

    public ItemHoeWood() {
        this(0, 1);
    }

    public ItemHoeWood(Integer meta) {
        this(meta, 1);
    }

    public ItemHoeWood(Integer meta, int count) {
        super(WOODEN_HOE, meta, count, "Wooden Hoe");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_WOODEN;
    }

    @Override
    public boolean isHoe() {
        return true;
    }

    @Override
    public int getTier() {
        return ItemTool.TIER_WOODEN;
    }
}
