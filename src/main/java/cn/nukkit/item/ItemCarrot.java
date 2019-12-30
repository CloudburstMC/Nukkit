package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockIds;
import cn.nukkit.utils.Identifier;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemCarrot extends ItemEdible {

    public ItemCarrot(Identifier id) {
        super(id);
    }

    @Override
    public Block getBlock() {
        return Block.get(BlockIds.CARROTS);
    }
}
