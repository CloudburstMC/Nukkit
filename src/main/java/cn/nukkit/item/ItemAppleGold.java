package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.math.Vector3;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemAppleGold extends ItemEdible {

    public ItemAppleGold() {
        this(0, 1);
    }

    public ItemAppleGold(Integer meta) {
        this(meta, 1);
    }

    public ItemAppleGold(Integer meta, int count) {
        super(GOLDEN_APPLE, meta, count, "Golden Apple");
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        return true;
    }
}
