package cn.nukkit.item;

import cn.nukkit.block.BlockRedstoneWire;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemRedstone extends Item {

    public ItemRedstone() {
        this(0, 1);
    }

    public ItemRedstone(Integer meta) {
        this(meta, 1);
    }

    public ItemRedstone(Integer meta, int count) {
        super(REDSTONE, meta, count, "Redstone");
        this.block = new BlockRedstoneWire();
    }

}
