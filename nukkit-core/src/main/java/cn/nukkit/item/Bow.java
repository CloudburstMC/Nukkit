package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Bow extends Tool {

    public Bow() {
        this(0, 1);
    }

    public Bow(Integer meta) {
        this(meta, 1);
    }

    public Bow(Integer meta, int count) {
        super(BOW, meta, count, "Bow");
    }

    @Override
    public int getMaxDurability() {
        return Tool.DURABILITY_BOW;
    }
}
