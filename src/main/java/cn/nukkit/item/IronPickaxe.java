package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class IronPickaxe extends Tool {

    public IronPickaxe() {
        this(0, 1);
    }

    public IronPickaxe(int meta) {
        this(meta, 1);
    }

    public IronPickaxe(int meta, int count) {
        super(IRON_PICKAXE, meta, count, "Iron Pickaxe");
    }

    @Override
    public int getMaxDurability() {
        return Tool.DURABILITY_IRON;
    }

    @Override
    public boolean isPickaxe() {
        return true;
    }

    @Override
    public int getTier() {
        return Tool.TIER_IRON;
    }
}
