package cn.nukkit.item;

import cn.nukkit.block.BlockDoorBirch;

public class ItemDoorBirch extends Item {
    public ItemDoorBirch() {
        this(0, 1);
    }

    public ItemDoorBirch(Integer meta) {
        this(meta, 1);
    }

    public ItemDoorBirch(Integer meta, int count) {
        super(BIRCH_DOOR, 0, count, "Birch Door");
        this.block = new BlockDoorBirch();
    }

}
