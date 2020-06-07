package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

public class ItemDoorJungle extends Item {
    public ItemDoorJungle() {
        this(0, 1);
    }

    public ItemDoorJungle(Integer meta) {
        this(meta, 1);
    }

    public ItemDoorJungle(Integer meta, int count) {
        super(JUNGLE_DOOR, 0, count, "Jungle Door");
        this.block = Block.get(BlockID.JUNGLE_DOOR_BLOCK);
    }

}
