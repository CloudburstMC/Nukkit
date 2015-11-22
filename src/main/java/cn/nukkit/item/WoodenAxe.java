package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class WoodenAxe extends Tool {

    public WoodenAxe() {
        this(0, 1);
    }

    public WoodenAxe(Integer meta) {
        this(meta, 1);
    }

    public WoodenAxe(Integer meta, int count) {
        super(WOODEN_AXE, meta, count, "Wooden Axe");
    }

    @Override
    public int getMaxDurability() {
        return Tool.DURABILITY_WOODEN;
    }

    @Override
    public boolean isAxe() {
        return true;
    }
}
