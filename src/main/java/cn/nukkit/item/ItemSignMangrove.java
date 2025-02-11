package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

public class ItemSignMangrove extends Item {

    public ItemSignMangrove() {
        this(0, 1);
    }

    public ItemSignMangrove(Integer meta) {
        this(meta, 1);
    }

    public ItemSignMangrove(Integer meta, int count) {
        super(MANGROVE_SIGN, 0, count, "Mangrove Sign");
        this.block = Block.get(BlockID.MANGROVE_STANDING_SIGN);
    }

    @Override
    public int getMaxStackSize() {
        return 16;
    }
}
