package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockIds;
import cn.nukkit.utils.Identifier;

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

    public ItemSkull(Identifier id) {
        super(id);
    }

    @Override
    public Block getBlock() {
        return Block.get(BlockIds.SKULL);
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
