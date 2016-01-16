package cn.nukkit.item;

import cn.nukkit.block.Block;

public class DarkOakDoor extends Item {
    public DarkOakDoor() {
        this(0, 1);
    }

    public DarkOakDoor(Integer meta) {
        this(meta, 1);
    }

    public DarkOakDoor(Integer meta, int count) {
        super(DARK_OAK_DOOR, 0, count, "Dark Oak Door");
        this.block = Block.get(Item.DARK_OAK_DOOR_BLOCK);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
