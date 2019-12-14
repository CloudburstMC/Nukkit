package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDoorBirch;

public class BlockDoorBirch extends BlockDoorWood {

    public BlockDoorBirch(int id, int meta) {
        super(id, meta);
    }

    @Override
    public String getName() {
        return "Birch Door Block";
    }

    @Override
    public Item toItem() {
        return new ItemDoorBirch();
    }
}
