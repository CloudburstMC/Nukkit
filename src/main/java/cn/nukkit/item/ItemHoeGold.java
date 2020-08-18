package cn.nukkit.item;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemHoeGold extends ItemTool {

    public ItemHoeGold() {
        this(0, 1);
    }

    public ItemHoeGold(Integer meta) {
        this(meta, 1);
    }

    public ItemHoeGold(Integer meta, int count) {
        super(GOLD_HOE, meta, count, "Gold Hoe");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_GOLD;
    }

    @Override
    public boolean isHoe() {
        return true;
    }

    @Override
    public int getTier() {
        return ItemTool.TIER_GOLD;
    }
}
