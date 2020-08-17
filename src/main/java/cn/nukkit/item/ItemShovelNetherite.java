package cn.nukkit.item;

/**
 * @author Kaooot
 * @version 1.0
 */
public class ItemShovelNetherite extends ItemTool {

    public ItemShovelNetherite() {
        this(0, 1);
    }

    public ItemShovelNetherite(Integer meta) {
        this(meta, 1);
    }

    public ItemShovelNetherite(Integer meta, int count) {
        super(NETHERITE_SHOVEL, meta, count, "Netherite Shovel");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_NETHERITE;
    }

    @Override
    public boolean isShovel() {
        return true;
    }

    @Override
    public int getTier() {
        return ItemTool.TIER_NETHERITE;
    }

    @Override
    public int getAttackDamage() {
        return 5;
    }
}