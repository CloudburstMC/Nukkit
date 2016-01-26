package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class GoldChestplate extends Armor {

    public GoldChestplate() {
        this(0, 1);
    }

    public GoldChestplate(Integer meta) {
        this(meta, 1);
    }

    public GoldChestplate(Integer meta, int count) {
        super(GOLD_CHESTPLATE, meta, count, "Gold Chestplate");
    }

    @Override
    public int getTier() {
        return Armor.TIER_GOLD;
    }

    @Override
    public boolean isChestplate() {
        return true;
    }
}
