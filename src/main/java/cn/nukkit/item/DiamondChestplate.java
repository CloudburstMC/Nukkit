package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class DiamondChestplate extends Armor {

    public DiamondChestplate() {
        this(0, 1);
    }

    public DiamondChestplate(int meta) {
        this(meta, 1);
    }

    public DiamondChestplate(int meta, int count) {
        super(DIAMOND_CHESTPLATE, meta, count, "Diamond Chestplate");
    }
}
