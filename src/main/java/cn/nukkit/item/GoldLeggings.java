package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class GoldLeggings extends Armor {

    public GoldLeggings() {
        this(0, 1);
    }

    public GoldLeggings(Integer meta) {
        this(meta, 1);
    }

    public GoldLeggings(Integer meta, int count) {
        super(GOLD_LEGGINGS, meta, count, "Gold Leggings");
    }

    @Override
    public int getTier() { return Armor.TIER_GOLD; }

    @Override
    public boolean isLeggings(){
        return true;
    }
}
