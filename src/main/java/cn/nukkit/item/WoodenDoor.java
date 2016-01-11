package cn.nukkit.item;

import cn.nukkit.block.Block;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class WoodenDoor extends Item {

    public WoodenDoor() {
        this(0, 1);
    }

    public WoodenDoor(Integer meta) {
        this(meta, 1);
    }

    public WoodenDoor(Integer meta, int count) {
        super(WOODEN_DOOR, 0, count, "Oak Door");
        this.block = Block.get(Item.WOODEN_DOOR_BLOCK);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
