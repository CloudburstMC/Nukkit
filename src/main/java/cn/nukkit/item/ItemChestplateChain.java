package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemChestplateChain extends ItemArmor {

    public ItemChestplateChain() {
        this(0, 1);
    }

    public ItemChestplateChain(Integer meta) {
        this(meta, 1);
    }

    public ItemChestplateChain(Integer meta, int count) {
        super(CHAIN_CHESTPLATE, meta, count, "Chain Chestplate");
    }

    @Override
    public int getTier() {
        return ItemArmor.TIER_CHAIN;
    }

    @Override
    public boolean isChestplate() {
        return true;
    }
}
