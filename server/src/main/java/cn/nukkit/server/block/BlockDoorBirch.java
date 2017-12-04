package cn.nukkit.server.block;

import cn.nukkit.server.item.Item;
import cn.nukkit.server.item.ItemDoorBirch;

public class BlockDoorBirch extends BlockDoorWood {

    public BlockDoorBirch() {
        this(0);
    }

    public BlockDoorBirch(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Birch Door Block";
    }

    @Override
    public int getId() {
        return BIRCH_DOOR_BLOCK;
    }

    @Override
    public Item toItem() {
        return new ItemDoorBirch();
    }
}
