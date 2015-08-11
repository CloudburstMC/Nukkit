package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ChainChestplate extends Armor {

    public ChainChestplate() {
        this(0, 1);
    }

    public ChainChestplate(int meta) {
        this(meta, 1);
    }

    public ChainChestplate(int meta, int count) {
        super(CHAIN_CHESTPLATE, meta, count, "Chain Chestplate");
    }
}
