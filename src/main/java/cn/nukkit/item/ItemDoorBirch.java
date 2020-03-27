package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockIds;
import cn.nukkit.utils.Identifier;

public class ItemDoorBirch extends Item {

    public ItemDoorBirch(Identifier id) {
        super(id);
    }


    @Override
    public Block getBlock() {
        return Block.get(BlockIds.BIRCH_DOOR);
    }
}
