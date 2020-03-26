package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

public class ItemDoorSpruce extends Item {
    public ItemDoorSpruce() {
        this(0, 1);
    }

    public ItemDoorSpruce(Integer meta) {
        this(meta, 1);
    }

    public ItemDoorSpruce(Integer meta, int count) {
        super(SPRUCE_DOOR, 0, count, "Spruce Door");
        this.block = Block.get(BlockID.SPRUCE_DOOR_BLOCK);
    }

}
