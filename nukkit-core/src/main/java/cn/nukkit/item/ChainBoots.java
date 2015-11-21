package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ChainBoots extends Armor {

    public ChainBoots() {
        this(0, 1);
    }

    public ChainBoots(Integer meta) {
        this(meta, 1);
    }

    public ChainBoots(Integer meta, int count) {
        super(CHAIN_BOOTS, meta, count, "Chainmail Boots");
    }
}
