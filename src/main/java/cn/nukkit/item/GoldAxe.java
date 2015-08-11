package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class GoldAxe extends Tool {

    public GoldAxe() {
        this(0, 1);
    }

    public GoldAxe(int meta) {
        this(meta, 1);
    }

    public GoldAxe(int meta, int count) {
        super(GOLD_AXE, meta, count, "Gold Axe");
    }

    @Override
    public int getMaxDurability() {
        return Tool.DURABILITY_GOLD;
    }

    @Override
    public boolean isAxe() {
        return true;
    }
}
