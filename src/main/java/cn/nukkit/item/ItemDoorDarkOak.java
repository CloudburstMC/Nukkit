package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

public class ItemDoorDarkOak extends Item {
    public ItemDoorDarkOak() {
        this(0, 1);
    }

    public ItemDoorDarkOak(Integer meta) {
        this(meta, 1);
    }

    public ItemDoorDarkOak(Integer meta, int count) {
        super(DARK_OAK_DOOR, 0, count, "Dark Oak Door");
        this.block = Block.get(BlockID.DARK_OAK_DOOR_BLOCK);
    }

}
