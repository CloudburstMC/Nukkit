package cn.nukkit.item;

import cn.nukkit.block.Block;

public class ItemDoorCherry extends Item {

    public ItemDoorCherry() {
        this(0, 1);
    }

    public ItemDoorCherry(Integer meta) {
        this(meta, 1);
    }

    public ItemDoorCherry(Integer meta, int count) {
        super(CHERRY_DOOR, 0, count, "Cherry Door");
        this.block = Block.get(CHERRY_DOOR_BLOCK);
    }
}
