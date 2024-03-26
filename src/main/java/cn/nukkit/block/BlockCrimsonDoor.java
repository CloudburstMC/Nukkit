package cn.nukkit.block;

import cn.nukkit.item.Item;

public class BlockCrimsonDoor extends BlockDoor {

    public BlockCrimsonDoor() {
        this(0);
    }

    public BlockCrimsonDoor(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Crimson Door";
    }

    @Override
    public int getId() {
        return CRIMSON_DOOR_BLOCK;
    }

    @Override
    public Item toItem() {
        return Item.get(Item.CRIMSON_DOOR);
    }
}
