package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class StoneAxe extends Tool {

    public StoneAxe() {
        this(0, 1);
    }

    public StoneAxe(Integer meta) {
        this(meta, 1);
    }

    public StoneAxe(Integer meta, int count) {
        super(STONE_AXE, meta, count, "Stone Axe");
    }

    @Override
    public int getMaxDurability() {
        return Tool.DURABILITY_STONE;
    }

    @Override
    public boolean isAxe() {
        return true;
    }

    @Override
    public int getTier() {
        return Tool.TIER_STONE;
    }
}
