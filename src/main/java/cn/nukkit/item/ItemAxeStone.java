package cn.nukkit.item;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemAxeStone extends ItemTool {

    public ItemAxeStone() {
        this(0, 1);
    }

    public ItemAxeStone(Integer meta) {
        this(meta, 1);
    }

    public ItemAxeStone(Integer meta, int count) {
        super(STONE_AXE, meta, count, "Stone Axe");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_STONE;
    }

    @Override
    public boolean isAxe() {
        return true;
    }

    @Override
    public int getTier() {
        return ItemTool.TIER_STONE;
    }

    @Override
    public int getAttackDamage() {
        return 4;
    }
}
