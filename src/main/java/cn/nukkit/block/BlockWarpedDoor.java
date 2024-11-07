package cn.nukkit.block;

import cn.nukkit.item.Item;

public class BlockWarpedDoor extends BlockDoor {

    public BlockWarpedDoor() {
        this(0);
    }

    public BlockWarpedDoor(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Warped Door";
    }

    @Override
    public int getId() {
        return WARPED_DOOR_BLOCK;
    }

    @Override
    public Item toItem() {
        return Item.get(Item.WARPED_DOOR);
    }
}
