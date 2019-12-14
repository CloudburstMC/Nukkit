package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDoorDarkOak;

public class BlockDoorDarkOak extends BlockDoorWood {

    public BlockDoorDarkOak(int id, int meta) {
        super(id, meta);
    }

    @Override
    public String getName() {
        return "Dark Oak Door Block";
    }

    @Override
    public Item toItem() {
        return new ItemDoorDarkOak();
    }
}
