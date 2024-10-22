package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class ItemSignAcacia extends Item {

    public ItemSignAcacia() {
        this(0, 1);
    }

    public ItemSignAcacia(Integer meta) {
        this(meta, 1);
    }

    public ItemSignAcacia(Integer meta, int count) {
        super(ACACIA_SIGN, 0, count, "Acacia Sign");
        this.block = Block.get(BlockID.ACACIA_STANDING_SIGN);
    }

    @Override
    public int getMaxStackSize() {
        return 16;
    }
}
