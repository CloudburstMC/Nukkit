package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.Identifier;

import static cn.nukkit.block.BlockIds.WOODEN_SLAB;

/**
 * Created on 2015/12/2 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockDoubleSlabWood extends BlockDoubleSlab {

    public BlockDoubleSlabWood(Identifier id) {
        super(id, WOODEN_SLAB, BlockSlabWood.COLORS);
    }

    @Override
    public float getHardness() {
        return 2;
    }

    @Override
    public float getResistance() {
        return 15;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }
}
