package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ChainLeggings extends Armor {

    public ChainLeggings() {
        this(0, 1);
    }

    public ChainLeggings(int meta) {
        this(meta, 1);
    }

    public ChainLeggings(int meta, int count) {
        super(CHAIN_LEGGINGS, meta, count, "Chain Leggings");
    }
}
