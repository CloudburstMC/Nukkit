package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class GoldPickaxe extends Tool {

    public GoldPickaxe() {
        this(0, 1);
    }

    public GoldPickaxe(int meta) {
        this(meta, 1);
    }

    public GoldPickaxe(int meta, int count) {
        super(GOLD_PICKAXE, meta, count, "Gold Pickaxe");
    }

    @Override
    public int getMaxDurability() {
        return Tool.DURABILITY_GOLD;
    }

    @Override
    public boolean isPickaxe() {
        return true;
    }
}
