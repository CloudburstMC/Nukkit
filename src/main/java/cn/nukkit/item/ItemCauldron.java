package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockIds;
import cn.nukkit.utils.Identifier;

/**
 * author: CreeperFace
 * Nukkit Project
 */
public class ItemCauldron extends Item {

    public ItemCauldron(Identifier id) {
        super(id);
    }

    @Override
    public Block getBlock() {
        return Block.get(BlockIds.CAULDRON);
    }
}