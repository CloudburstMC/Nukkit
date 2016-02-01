package cn.nukkit.item;

import cn.nukkit.block.Block;

public class SpruceDoor extends Item {
    public SpruceDoor() {
        this(0, 1);
    }

    public SpruceDoor(Integer meta) {
        this(meta, 1);
    }

    public SpruceDoor(Integer meta, int count) {
        super(SPRUCE_DOOR, 0, count, "Spruce Door");
        this.block = Block.get(Item.SPRUCE_DOOR_BLOCK);
    }

}
