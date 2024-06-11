package cn.nukkit.item;

import cn.nukkit.block.Block;

/**
 * Created by Snake1999 on 2016/2/3.
 * Package cn.nukkit.item in project Nukkit.
 */
public class ItemSkull extends Item {

    public static final int SKELETON_SKULL = 0;
    public static final int WITHER_SKELETON_SKULL = 1;
    public static final int ZOMBIE_HEAD = 2;
    public static final int HEAD = 3;
    public static final int CREEPER_HEAD = 4;
    public static final int DRAGON_HEAD = 5;
    public static final int PIGLIN_HEAD = 6;

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
            case WITHER_SKELETON_SKULL :
                return "Wither Skeleton Skull";
            case ZOMBIE_HEAD:
                return "Zombie Head";
            case HEAD:
                return "Head";
            case CREEPER_HEAD:
                return "Creeper Head";
            case DRAGON_HEAD:
                return "Dragon Head";
            case PIGLIN_HEAD:
                return "Piglin Head";
            case SKELETON_SKULL:
            default:
                return "Skeleton Skull";
        }
    }
}
