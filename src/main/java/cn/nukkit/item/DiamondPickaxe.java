package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class DiamondPickaxe extends Tool {

    public DiamondPickaxe() {
        this(0, 1);
    }

    public DiamondPickaxe(int meta) {
        this(meta, 1);
    }

    public DiamondPickaxe(int meta, int count) {
        super(DIAMOND_PICKAXE, meta, count, "Diamond Pickaxe");
    }

    @Override
    public int getMaxDurability() {
        return Tool.DURABILITY_DIAMOND;
    }

    @Override
    public boolean isPickaxe() {
        return true;
    }

    @Override
    public int getTier() {
        return Tool.TIER_DIAMOND;
    }
}
