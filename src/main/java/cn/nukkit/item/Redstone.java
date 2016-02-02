package cn.nukkit.item;

import cn.nukkit.block.RedstoneWire;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Redstone extends Item {

    public Redstone() {
        this(0, 1);
    }

    public Redstone(Integer meta) {
        this(meta, 1);
    }

    public Redstone(Integer meta, int count) {
        super(REDSTONE, meta, count, "Redstone");
        this.block = new RedstoneWire();
    }

}
