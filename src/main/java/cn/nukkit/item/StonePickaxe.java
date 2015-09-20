package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class StonePickaxe extends Tool {

    public StonePickaxe() {
        this(0, 1);
    }

    public StonePickaxe(int meta) {
        this(meta, 1);
    }

    public StonePickaxe(int meta, int count) {
        super(STONE_PICKAXE, meta, count, "Stone Pickaxe");
    }

    @Override
    public int getMaxDurability() {
        return Tool.DURABILITY_STONE;
    }

    @Override
    public boolean isPickaxe() {
        return true;
    }

    @Override
    public int getTier() {
        return Tool.TIER_STONE;
    }
}
