package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockIds;
import cn.nukkit.utils.Identifier;

/**
 * Created by Snake1999 on 2016/2/4.
 * Package cn.nukkit.item in project Nukkit.
 */
public class ItemFlowerPot extends Item {

    public ItemFlowerPot(Identifier id) {
        super(id);
    }

    @Override
    public Block getBlock() {
        return Block.get(BlockIds.FLOWER_POT);
    }
}
