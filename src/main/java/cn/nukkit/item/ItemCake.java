package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

/**
 * @author MagicDroidX (Nukkit Project)
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
        this.block = Block.get(BlockID.CAKE_BLOCK);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
