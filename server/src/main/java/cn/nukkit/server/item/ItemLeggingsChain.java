package cn.nukkit.server.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemLeggingsChain extends ItemArmor {

    public ItemLeggingsChain() {
        this(0, 1);
    }

    public ItemLeggingsChain(Integer meta) {
        this(meta, 1);
    }

    public ItemLeggingsChain(Integer meta, int count) {
        super(CHAIN_LEGGINGS, meta, count, "Chain Leggings");
    }

    @Override
    public int getTier() {
        return ItemArmor.TIER_CHAIN;
    }

    @Override
    public boolean isLeggings() {
        return true;
    }

    @Override
    public int getArmorPoints() {
        return 4;
    }

    @Override
    public int getMaxDurability() {
        return 226;
    }
}
