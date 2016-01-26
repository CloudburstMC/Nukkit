package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ChainHelmet extends Armor {

    public ChainHelmet() {
        this(0, 1);
    }

    public ChainHelmet(Integer meta) {
        this(meta, 1);
    }

    public ChainHelmet(Integer meta, int count) {
        super(CHAIN_HELMET, meta, count, "Chainmail Helmet");
    }

    @Override
    public int getTier() { return Armor.TIER_CHAIN; }

    @Override
    public boolean isHelmet(){
        return true;
    }
}
