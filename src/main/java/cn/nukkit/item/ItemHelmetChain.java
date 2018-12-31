package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemHelmetChain extends ItemArmor {

    public ItemHelmetChain() {
        this(0, 1);
    }

    public ItemHelmetChain(Integer meta) {
        this(meta, 1);
    }

    public ItemHelmetChain(Integer meta, int count) {
        super(CHAIN_HELMET, meta, count, "Chainmail Helmet");
    }

    @Override
    public int getTier() {
        return ItemArmor.TIER_CHAIN;
    }

    @Override
    public boolean isHelmet() {
        return true;
    }

    @Override
    public int getMaxDurability() {
        return 166;
    }
}
