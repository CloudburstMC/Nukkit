package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDoorSpruce;

public class BlockDoorSpruce extends BlockDoorWood {

    public BlockDoorSpruce(int id, int meta) {
        super(id, meta);
    }

    @Override
    public String getName() {
        return "Spruce Door Block";
    }

    @Override
    public Item toItem() {
        return new ItemDoorSpruce();
    }
}
