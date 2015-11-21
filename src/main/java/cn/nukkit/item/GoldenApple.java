package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class GoldenApple extends Item {

    public GoldenApple() {
        this(0, 1);
    }

    public GoldenApple(Integer meta) {
        this(meta, 1);
    }

    public GoldenApple(Integer meta, int count) {
        super(GOLDEN_APPLE, meta, count, "Golden Apple");
    }
}
