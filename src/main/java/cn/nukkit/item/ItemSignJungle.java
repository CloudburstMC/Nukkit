package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class ItemSignJungle extends Item {

    public ItemSignJungle() {
        this(0, 1);
    }

    public ItemSignJungle(Integer meta) {
        this(meta, 1);
    }

    public ItemSignJungle(Integer meta, int count) {
        super(JUNGLE_SIGN, 0, count, "Jungle Sign");
        this.block = Block.get(BlockID.JUNGLE_STANDING_SIGN);
    }

    @Override
    public int getMaxStackSize() {
        return 16;
    }
}
