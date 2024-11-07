package cn.nukkit.item;

import cn.nukkit.block.Block;

public class ItemCampfireSoul extends Item {

    public ItemCampfireSoul() {
        this(0, 1);
    }

    public ItemCampfireSoul(Integer meta) {
        this(meta, 1);
    }

    public ItemCampfireSoul(Integer meta, int count) {
        super(SOUL_CAMPFIRE, meta, count, "Soul Campfire");
        this.block = Block.get(Block.SOUL_CAMPFIRE_BLOCK);
    }
}
