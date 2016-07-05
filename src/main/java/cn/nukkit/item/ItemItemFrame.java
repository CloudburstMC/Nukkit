package cn.nukkit.item;

import cn.nukkit.block.BlockItemFrame;

/**
 * Created by Pub4Game on 03.07.2016.
 */
public class ItemItemFrame extends Item {

    public ItemItemFrame() {
        this(0, 1);
    }

    public ItemItemFrame(Integer meta) {
        this(meta, 1);
    }

    public ItemItemFrame(Integer meta, int count) {
        super(ITEM_FRAME, meta, count, "Item Frame");
        this.block = new BlockItemFrame();
    }
}
