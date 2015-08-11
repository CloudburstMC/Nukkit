package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class GoldIngot extends Item {

    public GoldIngot() {
        this(0, 1);
    }

    public GoldIngot(int meta) {
        this(meta, 1);
    }

    public GoldIngot(int meta, int count) {
        super(GOLD_INGOT, 0, count, "Gold Ingot");
    }
}
