package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemCarrot extends ItemEdible {

    public ItemCarrot() {
        this(0, 1);
    }

    public ItemCarrot(Integer meta) {
        this(meta, 1);
    }

    public ItemCarrot(Integer meta, int count) {
        super(CARROT, 0, count, "Carrot");
        this.block = Block.get(BlockID.CARROT_BLOCK);
    }

}
