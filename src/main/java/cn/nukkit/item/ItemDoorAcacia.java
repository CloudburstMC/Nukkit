package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockIds;
import cn.nukkit.utils.Identifier;

public class ItemDoorAcacia extends Item {

    public ItemDoorAcacia(Identifier id) {
        super(id);
    }

    @Override
    public Block getBlock() {
        return Block.get(BlockIds.ACACIA_DOOR);
    }
}
