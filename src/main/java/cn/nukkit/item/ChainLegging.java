package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ChainLegging extends Armor {

    public ChainLegging() {
        this(0, 1);
    }

    public ChainLegging(int meta) {
        this(meta, 1);
    }

    public ChainLegging(int meta, int count) {
        super(CHAIN_LEGGINGS, meta, count, "Chain Leggings");
    }
}
