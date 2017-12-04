package cn.nukkit.server.item;

import cn.nukkit.server.block.BlockCake;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemCake extends Item {

    public ItemCake() {
        this(0, 1);
    }

    public ItemCake(Integer meta) {
        this(meta, 1);
    }

    public ItemCake(Integer meta, int count) {
        super(CAKE, 0, count, "Cake");
        this.block = new BlockCake();
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
