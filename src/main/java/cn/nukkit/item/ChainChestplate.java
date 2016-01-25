package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ChainChestplate extends Armor {

    public ChainChestplate() {
        this(0, 1);
    }

    public ChainChestplate(Integer meta) {
        this(meta, 1);
    }

    public ChainChestplate(Integer meta, int count) {
        super(CHAIN_CHESTPLATE, meta, count, "Chain Chestplate");
    }

    @Override
    public int getTier() { return Armor.TIER_CHAIN; }

    @Override
    public boolean isChestplate(){
        return true;
    }
}
