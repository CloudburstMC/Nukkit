package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockIds;
import cn.nukkit.utils.Identifier;

/**
 * Created by CreeperFace on 13.5.2017.
 */
public class ItemHopper extends Item {

    public ItemHopper(Identifier id) {
        super(id);
    }

    @Override
    public Block getBlock() {
        return Block.get(BlockIds.HOPPER);
    }
}
