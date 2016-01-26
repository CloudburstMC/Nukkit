package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class DiamondChestplate extends Armor {

    public DiamondChestplate() {
        this(0, 1);
    }

    public DiamondChestplate(Integer meta) {
        this(meta, 1);
    }

    public DiamondChestplate(Integer meta, int count) {
        super(DIAMOND_CHESTPLATE, meta, count, "Diamond Chestplate");
    }

    @Override
    public int getTier() {
        return Armor.TIER_DIAMOND;
    }

    @Override
    public boolean isChestplate() {
        return true;
    }
}
