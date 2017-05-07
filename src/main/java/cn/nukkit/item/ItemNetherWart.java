package cn.nukkit.item;

import cn.nukkit.block.BlockNetherWart;

/**
 * Created by Leonidius20 on 22.03.17.
 */
public class ItemNetherWart extends Item {

    public ItemNetherWart() {
        this(0, 1);
    }

    public ItemNetherWart(Integer meta) {
        this(meta, 1);
    }

    public ItemNetherWart(Integer meta, int count) {
        super(NETHER_WART, meta, count, "Nether Wart");
        this.block = new BlockNetherWart(meta);
    }

}
