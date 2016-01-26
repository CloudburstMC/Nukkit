package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Shears extends Tool {

    public Shears() {
        this(0, 1);
    }

    public Shears(Integer meta) {
        this(meta, 1);
    }

    public Shears(Integer meta, int count) {
        super(SHEARS, meta, count, "Shears");
    }

    @Override
    public int getMaxDurability() {
        return Tool.DURABILITY_SHEARS;
    }

    @Override
    public boolean isShears() {
        return true;
    }
}
