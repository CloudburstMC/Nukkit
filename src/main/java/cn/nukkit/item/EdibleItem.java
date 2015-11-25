package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EdibleItem {
    public static int getRegainAmount(int id) {
        return getRegainAmount(id, 0);
    }

    public static int getRegainAmount(int id, int meta) {
        switch (id) {
            case Item.APPLE:
                return 4;
            case Item.MUSHROOM_STEW:
                return 10;
            case Item.BEETROOT_SOUP:
                return 10;
            case Item.BREAD:
                return 5;
            case Item.RAW_PORKCHOP:
                return 3;
            case Item.COOKED_PORKCHOP:
                return 8;
            case Item.RAW_BEEF:
                return 3;
            case Item.STEAK:
                return 8;
            case Item.COOKED_CHICKEN:
                return 6;
            case Item.RAW_CHICKEN:
                return 2;
            case Item.MELON_SLICE:
                return 2;
            case Item.GOLDEN_APPLE:
                return 10;
            case Item.PUMPKIN_PIE:
                return 8;
            case Item.CARROT:
                return 4;
            case Item.POTATO:
                return 1;
            case Item.BAKED_POTATO:
                return 6;
            case Item.COOKIE:
                return 2;
            case Item.COOKED_FISH:
                switch (meta) {
                    case 1:
                        return 6;
                    default:
                        return 5;
                }
            case Item.RAW_FISH:
                switch (meta) {
                    case 1:
                        return 2;
                    case 2:
                        return 1;
                    case 3:
                        return 1;
                    default:
                        return 2;
                }
        }
        return 0;
    }
}
