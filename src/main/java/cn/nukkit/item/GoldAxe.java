package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class GoldAxe extends Tool {

    public GoldAxe() {
        this(0, 1);
    }

    public GoldAxe(Integer meta) {
        this(meta, 1);
    }

    public GoldAxe(Integer meta, int count) {
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

    @Override
    public int getTier() {
        return Tool.TIER_GOLD;
    }
}
