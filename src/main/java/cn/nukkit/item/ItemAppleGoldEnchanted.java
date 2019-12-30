package cn.nukkit.item;

import cn.nukkit.math.Vector3;
import cn.nukkit.player.Player;
import cn.nukkit.utils.Identifier;

/**
 * Created by Snake1999 on 2016/1/14.
 * Package cn.nukkit.item in project nukkit.
 */
public class ItemAppleGoldEnchanted extends ItemEdible {

    public ItemAppleGoldEnchanted(Identifier id) {
        super(id);
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        return true;
    }
}
