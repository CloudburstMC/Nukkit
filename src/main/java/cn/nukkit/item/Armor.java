package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
abstract public class Armor extends Item {

    public static final int TIER_LEATHER = 1;
    public static final int TIER_IRON = 2;
    public static final int TIER_CHAIN = 3;
    public static final int TIER_GOLD = 4;
    public static final int TIER_DIAMOND = 5;

    public Armor(int id) {
        super(id);
    }

    public Armor(int id, int meta) {
        super(id, meta);
    }

    public Armor(int id, int meta, int count) {
        super(id, meta, count);
    }

    public Armor(int id, int meta, int count, String name) {
        super(id, meta, count, name);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean isArmor() {
        return true;
    }
}
