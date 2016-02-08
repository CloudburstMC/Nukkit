package cn.nukkit.item;

import cn.nukkit.block.BlockDoorJungle;

public class ItemDoorJungle extends Item {
    public ItemDoorJungle() {
        this(0, 1);
    }

    public ItemDoorJungle(Integer meta) {
        this(meta, 1);
    }

    public ItemDoorJungle(Integer meta, int count) {
        super(JUNGLE_DOOR, 0, count, "Jungle Door");
        this.block = new BlockDoorJungle();
    }

}
