package cn.nukkit.server.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemShovelDiamond extends ItemTool {

    public ItemShovelDiamond() {
        this(0, 1);
    }

    public ItemShovelDiamond(Integer meta) {
        this(meta, 1);
    }

    public ItemShovelDiamond(Integer meta, int count) {
        super(DIAMOND_SHOVEL, meta, count, "Diamond Shovel");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_DIAMOND;
    }

    @Override
    public boolean isShovel() {
        return true;
    }

    @Override
    public int getTier() {
        return ItemTool.TIER_DIAMOND;
    }

    @Override
    public int getAttackDamage() {
        return 4;
    }
}
