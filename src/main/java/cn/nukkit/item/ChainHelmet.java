package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ChainHelmet extends Armor {

    public ChainHelmet() {
        this(0, 1);
    }

    public ChainHelmet(int meta) {
        this(meta, 1);
    }

    public ChainHelmet(int meta, int count) {
        super(CHAIN_HELMET, meta, count, "Chainmail Helmet");
    }
}
