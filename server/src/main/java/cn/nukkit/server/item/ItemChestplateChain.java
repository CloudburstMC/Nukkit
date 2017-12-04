package cn.nukkit.server.item;

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

    @Override
    public int getArmorPoints() {
        return 5;
    }

    @Override
    public int getMaxDurability() {
        return 241;
    }
}
