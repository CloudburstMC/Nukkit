package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Identifier;

/**
 * @author Erik Miller | EinBexiii | Bex
 */
public class BlockStairsDiorite extends BlockStairs {

    public BlockStairsDiorite(Identifier id) {
        super(id);
    }

    @Override
    public float getHardness() {
        return 1.5f;
    }

    @Override
    public float getResistance() {
        return 30;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.QUARTZ_BLOCK_COLOR;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

}
