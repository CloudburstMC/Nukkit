package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.utils.Identifier;

public class BlockStrippedLog extends BlockLog {

    public BlockStrippedLog(Identifier identifier) {
        super(identifier);
    }

    @Override
    public boolean canBeActivated() {
        return false;
    }

    @Override
    public Item toItem() {
        return Item.get(id);
    }
}
