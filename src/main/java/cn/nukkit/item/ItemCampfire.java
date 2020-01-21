package cn.nukkit.item;

import cn.nukkit.block.BlockCampfire;

public class ItemCampfire extends Item {

    public ItemCampfire() {
        this(0, 1);
    }

    public ItemCampfire(Integer meta) {
        this(meta, 1);
    }

    public ItemCampfire(Integer meta, int count) {
        super(CAMPFIRE, meta, count, "Campfire");
        this.block = new BlockCampfire();
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
