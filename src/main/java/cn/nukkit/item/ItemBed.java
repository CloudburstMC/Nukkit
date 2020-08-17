package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.utils.DyeColor;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemBed extends Item {

    public ItemBed() {
        this(0, 1);
    }

    public ItemBed(Integer meta) {
        this(meta, 1);
    }

    public ItemBed(Integer meta, int count) {
        super(BED, meta, count, DyeColor.getByWoolData(meta).getName() + " Bed");
        this.block = Block.get(BlockID.BED_BLOCK);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
