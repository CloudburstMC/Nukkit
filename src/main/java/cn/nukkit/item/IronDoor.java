package cn.nukkit.item;

import cn.nukkit.block.Block;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class IronDoor extends Item {

    public IronDoor() {
        this(0, 1);
    }

    public IronDoor(int meta) {
        this(meta, 1);
    }

    public IronDoor(int meta, int count) {
        super(IRON_DOOR, 0, count, "Iron Door");
        this.block = Block.get(Item.IRON_DOOR_BLOCK);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
