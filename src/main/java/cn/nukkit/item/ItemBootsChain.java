package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemBootsChain extends ItemArmor {

    public ItemBootsChain() {
        this(0, 1);
    }

    public ItemBootsChain(Integer meta) {
        this(meta, 1);
    }

    public ItemBootsChain(Integer meta, int count) {
        super(CHAIN_BOOTS, meta, count, "Chainmail Boots");
    }

    @Override
    public int getTier() {
        return ItemArmor.TIER_CHAIN;
    }

    @Override
    public boolean isBoots() {
        return true;
    }
}
