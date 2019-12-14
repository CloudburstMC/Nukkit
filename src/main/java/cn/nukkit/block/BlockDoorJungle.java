package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDoorJungle;

public class BlockDoorJungle extends BlockDoorWood {

    public BlockDoorJungle(int id, int meta) {
        super(id, meta);
    }

    @Override
    public String getName() {
        return "Jungle Door Block";
    }

    @Override
    public Item toItem() {
        return new ItemDoorJungle();
    }
}
