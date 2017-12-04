package cn.nukkit.server.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemSwordStone extends ItemTool {

    public ItemSwordStone() {
        this(0, 1);
    }

    public ItemSwordStone(Integer meta) {
        this(meta, 1);
    }

    public ItemSwordStone(Integer meta, int count) {
        super(STONE_SWORD, meta, count, "Stone Sword");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_STONE;
    }

    @Override
    public boolean isSword() {
        return true;
    }

    @Override
    public int getTier() {
        return ItemTool.TIER_STONE;
    }

    @Override
    public int getAttackDamage() {
        return 5;
    }
}
