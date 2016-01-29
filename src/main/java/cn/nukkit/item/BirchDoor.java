package cn.nukkit.item;

import cn.nukkit.block.Block;

public class BirchDoor extends Item {
    public BirchDoor() {
        this(0, 1);
    }

    public BirchDoor(Integer meta) {
        this(meta, 1);
    }

    public BirchDoor(Integer meta, int count) {
        super(BIRCH_DOOR, 0, count, "Birch Door");
        this.block = Block.get(Item.BIRCH_DOOR_BLOCK);
    }
}
