package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDoorWood;

public class BlockDoorOak extends BlockDoorWood {

    public BlockDoorOak(int id, int meta) {
        super(id, meta);
    }

    @Override
    public String getName() {
        return "Oak Door Block";
    }

    @Override
    public Item toItem() {
        return new ItemDoorWood();
    }
}
