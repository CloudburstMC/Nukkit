package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

public class ItemSignCherry extends Item {

    public ItemSignCherry() {
        this(0, 1);
    }

    public ItemSignCherry(Integer meta) {
        this(meta, 1);
    }

    public ItemSignCherry(Integer meta, int count) {
        super(CHERRY_SIGN, 0, count, "Cherry Sign");
        this.block = Block.get(BlockID.CHERRY_STANDING_SIGN);
    }

    @Override
    public int getMaxStackSize() {
        return 16;
    }
}
