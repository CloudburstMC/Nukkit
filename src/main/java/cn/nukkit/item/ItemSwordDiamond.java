package cn.nukkit.item;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemSwordDiamond extends ItemTool {

    public ItemSwordDiamond() {
        this(0, 1);
    }

    public ItemSwordDiamond(Integer meta) {
        this(meta, 1);
    }

    public ItemSwordDiamond(Integer meta, int count) {
        super(DIAMOND_SWORD, meta, count, "Diamond Sword");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_DIAMOND;
    }

    @Override
    public boolean isSword() {
        return true;
    }

    @Override
    public int getTier() {
        return ItemTool.TIER_DIAMOND;
    }

    @Override
    public int getAttackDamage() {
        return 7;
    }
}
