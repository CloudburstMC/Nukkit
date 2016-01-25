package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class GoldHelmet extends Armor {

    public GoldHelmet() {
        this(0, 1);
    }

    public GoldHelmet(Integer meta) {
        this(meta, 1);
    }

    public GoldHelmet(Integer meta, int count) {
        super(GOLD_HELMET, meta, count, "Gold Helmet");
    }

    @Override
    public int getTier() { return Armor.TIER_GOLD; }

    @Override
    public boolean isHelmet(){
        return true;
    }
}
