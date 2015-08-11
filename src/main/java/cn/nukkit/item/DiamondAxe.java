package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class DiamondAxe extends Tool {

    public DiamondAxe() {
        this(0, 1);
    }

    public DiamondAxe(int meta) {
        this(meta, 1);
    }

    public DiamondAxe(int meta, int count) {
        super(DIAMOND_AXE, meta, count, "Diamond Axe");
    }

    @Override
    public int getMaxDurability() {
        return Tool.DURABILITY_DIAMOND;
    }

    @Override
    public boolean isAxe() {
        return true;
    }
}
