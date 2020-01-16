package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.math.Vector3;

/**
 * Created by Snake1999 on 2016/1/14.
 * Package cn.nukkit.item in project nukkit.
 */
public class ItemAppleGoldEnchanted extends ItemEdible {
    public ItemAppleGoldEnchanted() {
        this(0, 1);
    }

    public ItemAppleGoldEnchanted(Integer meta) {
        this(meta, 1);
    }

    public ItemAppleGoldEnchanted(Integer meta, int count) {
        super(GOLDEN_APPLE_ENCHANTED, meta, count, "Enchanted Golden Apple");
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        return true;
    }
}
