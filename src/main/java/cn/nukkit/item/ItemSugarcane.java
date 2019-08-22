package cn.nukkit.item;

import cn.nukkit.block.BlockSugarcane;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemSugarcane extends Item {

    public ItemSugarcane() {
        this(0, 1);
    }

    public ItemSugarcane(Integer meta) {
        this(meta, 1);
    }

    public ItemSugarcane(Integer meta, int count) {
        super(SUGARCANE, 0, count, "Sugar Canes");
        this.block = new BlockSugarcane();
    }
}
