package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.player.Player;
import cn.nukkit.utils.Identifier;

/**
 * Created on 2015/11/22 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockPodzol extends BlockDirt {

    public BlockPodzol(Identifier id) {
        super(id);
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public boolean canBeActivated() {
        return false;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        return false;
    }
}
