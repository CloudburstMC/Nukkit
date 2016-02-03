package cn.nukkit.item;

import cn.nukkit.block.BlockBed;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Bed extends Item {

    public Bed() {
        this(0, 1);
    }

    public Bed(Integer meta) {
        this(meta, 1);
    }

    public Bed(Integer meta, int count) {
        super(BED, 0, count, "BlockBed");
        this.block = new BlockBed();
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
