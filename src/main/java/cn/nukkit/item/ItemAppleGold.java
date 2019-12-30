package cn.nukkit.item;

import cn.nukkit.math.Vector3;
import cn.nukkit.player.Player;
import cn.nukkit.utils.Identifier;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemAppleGold extends ItemEdible {

    public ItemAppleGold(Identifier id) {
        super(id);
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        return true;
    }
}
