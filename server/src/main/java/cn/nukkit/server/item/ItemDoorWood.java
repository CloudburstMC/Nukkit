package cn.nukkit.server.item;

import cn.nukkit.server.block.BlockDoorWood;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemDoorWood extends Item {

    public ItemDoorWood() {
        this(0, 1);
    }

    public ItemDoorWood(Integer meta) {
        this(meta, 1);
    }

    public ItemDoorWood(Integer meta, int count) {
        super(WOODEN_DOOR, 0, count, "Oak Door");
        this.block = new BlockDoorWood();
    }

}
