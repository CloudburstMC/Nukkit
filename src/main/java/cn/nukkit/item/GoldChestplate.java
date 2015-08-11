package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class GoldChestplate extends Armor {

    public GoldChestplate() {
        this(0, 1);
    }

    public GoldChestplate(int meta) {
        this(meta, 1);
    }

    public GoldChestplate(int meta, int count) {
        super(GOLD_CHESTPLATE, meta, count, "Gold Chestplate");
    }
}
