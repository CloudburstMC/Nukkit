package cn.nukkit.item;

public class ItemCrossbow extends ItemTool {

    public ItemCrossbow() {
        this(0, 1);
    }

    public ItemCrossbow(Integer meta) {
        this(meta, 1);
    }

    public ItemCrossbow(Integer meta, int count) {
        super(CROSSBOW, meta, count, "Crossbow");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_CROSSBOW;
    }

    @Override
    public int getEnchantAbility() {
        return 1;
    }
}
