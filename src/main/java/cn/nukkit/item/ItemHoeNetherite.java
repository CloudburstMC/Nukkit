package cn.nukkit.item;

public class ItemHoeNetherite extends ItemTool {

    public ItemHoeNetherite() {
        this(0, 1);
    }

    public ItemHoeNetherite(Integer meta) {
        this(meta, 1);
    }

    public ItemHoeNetherite(Integer meta, int count) {
        super(NETHERITE_HOE, meta, count, "Netherite Hoe");
    }

    @Override
    public boolean isHoe() {
        return true;
    }

    @Override
    public int getAttackDamage() {
        return 6;
    }

    @Override
    public int getTier() {
        return ItemTool.TIER_NETHERITE;
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_NETHERITE;
    }
}
