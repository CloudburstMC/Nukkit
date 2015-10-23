package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class WoodenPickaxe extends Tool {

    public WoodenPickaxe() {
        this(0, 1);
    }

    public WoodenPickaxe(Integer meta) {
        this(meta, 1);
    }

    public WoodenPickaxe(Integer meta, int count) {
        super(WOODEN_PICKAXE, meta, count, "Wooden Pickaxe");
    }

    @Override
    public int getMaxDurability() {
        return Tool.DURABILITY_WOODEN;
    }

    @Override
    public boolean isPickaxe() {
        return true;
    }
}
