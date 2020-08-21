package cn.nukkit.item;

import cn.nukkit.block.Block;

/**
 * @author Snake1999
 * @since 2016/2/3
 */
public class ItemSkull extends Item {
    public static final int SKELETON_SKULL = 0;
    public static final int WITHER_SKELETON_SKULL = 1;
    public static final int ZOMBIE_HEAD = 2;
    public static final int HEAD = 3;
    public static final int CREEPER_HEAD = 4;
    public static final int DRAGON_HEAD = 5;

    public ItemSkull() {
        this(0, 1);
    }

    public ItemSkull(Integer meta) {
        this(meta, 1);
    }

    public ItemSkull(Integer meta, int count) {
        super(SKULL, meta, count, getItemSkullName(meta));
        this.block = Block.get(Block.SKULL_BLOCK);
    }

    public static String getItemSkullName(int meta) {
        switch (meta) {
            case 1:
                return "Wither Skeleton Skull";
            case 2:
                return "Zombie Head";
            case 3:
                return "Head";
            case 4:
                return "Creeper Head";
            case 5:
                return "Dragon Head";
            case 0:
            default:
                return "Skeleton Skull";
        }
    }
}
