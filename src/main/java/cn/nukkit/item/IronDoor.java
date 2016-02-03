package cn.nukkit.item;

import cn.nukkit.block.BlockDoorIron;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class IronDoor extends Item {

    public IronDoor() {
        this(0, 1);
    }

    public IronDoor(Integer meta) {
        this(meta, 1);
    }

    public IronDoor(Integer meta, int count) {
        super(IRON_DOOR, 0, count, "Iron Door");
        this.block = new BlockDoorIron();
    }

}
